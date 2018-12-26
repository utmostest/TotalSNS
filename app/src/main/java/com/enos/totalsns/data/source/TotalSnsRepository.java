package com.enos.totalsns.data.source;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.database.Observable;
import android.databinding.ObservableField;
import android.util.Log;

import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.source.local.TotalSnsDatabase;
import com.enos.totalsns.data.source.remote.OauthToken;
import com.enos.totalsns.data.source.remote.TwitterManager;
import com.enos.totalsns.intro.LoginResult;
import com.enos.totalsns.login.OnTwitterInitListener;
import com.enos.totalsns.login.OnTwitterLoginListener;
import com.enos.totalsns.timelines.OnTimelineResult;
import com.enos.totalsns.util.AppExecutors;
import com.enos.totalsns.util.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.User;

/**
 * Repository handling the work with products and comments.
 */
public class TotalSnsRepository {

    //TODO SNS Client Repository 생성 및 SNS별 기능 추가
    private static TotalSnsRepository sInstance;

    private MediatorLiveData<List<Account>> mObservableAccounts;

    private TwitterManager mTwitterManager;

    private AppExecutors mAppExecutors;
    private TotalSnsDatabase mDatabase;

    private SingleLiveEvent<LoginResult> loginResult;

    private TotalSnsRepository(final TotalSnsDatabase database, final TwitterManager twitterManager) {
        mDatabase = database;
        mTwitterManager = twitterManager;
        mObservableAccounts = new MediatorLiveData<>();

        mObservableAccounts.addSource(mDatabase.accountDao().loadAccounts(),
                accounts -> mObservableAccounts.postValue(accounts));

        mAppExecutors = new AppExecutors();

        loginResult = new SingleLiveEvent<LoginResult>();
    }

    public static TotalSnsRepository getInstance(final TotalSnsDatabase database, final TwitterManager twitterManager) {
        if (sInstance == null) {
            synchronized (TotalSnsRepository.class) {
                if (sInstance == null) {
                    sInstance = new TotalSnsRepository(database, twitterManager);
                }
            }
        }
        return sInstance;
    }

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public LiveData<List<Account>> getAccounts() {
        return mObservableAccounts;
    }

    public void init() {
        mAppExecutors.networkIO().execute(() -> {
            mTwitterManager.init();
            LoginResult oldResult = loginResult.getValue();
            if (oldResult == null) oldResult = new LoginResult();
            oldResult.setLoginStep(LoginResult.STEP1_INIT);
            String url = null;
            try {
                url = mTwitterManager.getAuthorizationUrl();
                oldResult.setLoginStatus(LoginResult.STATUS_LOGIN_SUCCEED);
                oldResult.setAuthorizationUrl(url);
                loginResult.postValue(oldResult);
            } catch (TwitterException e) {
                oldResult.setLoginStatus(LoginResult.STATUS_LOGIN_FAILED);
                oldResult.setMessage(e.getMessage());
                loginResult.postValue(oldResult);
            }
        });
    }

    public void signInTwitterWithSaved(boolean isEnableUpdate) {
        mAppExecutors.networkIO().execute(() -> {
            mTwitterManager.init();
            Account current = mDatabase.accountDao().getCurrentAccountsBySns(Constants.TWITTER);
            if (current != null) {
                OauthToken oauthToken = new OauthToken(current.getOauthKey(), current.getOauthSecret());
                oauthToken.setIsAccessToken(true);
                signInTwitterWithOauthToken(oauthToken, isEnableUpdate);
            } else {
                LoginResult oldResult = loginResult.getValue();
                if (oldResult == null) oldResult = new LoginResult();
                oldResult.setLoginStep(LoginResult.STEP3_ENTIRELOGIN);
                oldResult.setLoginStatus(LoginResult.STATUS_LOGIN_FAILED);
                oldResult.setMessage("you don't have signed in account");
                loginResult.postValue(oldResult);
            }
        });
    }

    public void signInTwitterWithAccount(Account account, boolean isEnableUpdate) {
        signInTwitterWithOauthToken(new OauthToken(account), isEnableUpdate);
    }

    public void signInTwitterWithOauthToken(OauthToken token, boolean isEnableUpdate) {
        mAppExecutors.networkIO().execute(() -> {
            LoginResult oldResult = loginResult.getValue();
            if (oldResult == null) oldResult = new LoginResult();
            oldResult.setLoginStep(LoginResult.STEP3_ENTIRELOGIN);

            try {
                if (token == null || token.getToken() == null || token.getSecret() == null) {
                    oldResult.setMessage("oauth isn't valid");
                    loginResult.postValue(oldResult);
                    return;
                }

                Account account = mTwitterManager.signInWithOauthToken(token);
                if (isEnableUpdate) {
                    mAppExecutors.diskIO().execute(() -> mDatabase.updateCurrentUser(account, Constants.TWITTER));
                }
                oldResult.setLoginStatus(LoginResult.STATUS_LOGIN_SUCCEED);
                loginResult.postValue(oldResult);
                return;

            } catch (TwitterException e) {
                oldResult.setLoginStatus(LoginResult.STATUS_LOGIN_FAILED);
                oldResult.setMessage(e.getMessage());
                loginResult.postValue(oldResult);
            }
        });
    }

    public void signOut() {
        mAppExecutors.networkIO().execute(() -> {
            mDatabase.accountDao().updateSignOutBySns(Constants.TWITTER);
            mTwitterManager.signOut();
        });
    }

    public LiveData<ArrayList<Article>> getHomeTimeline(Paging paging) {
        fetchTimeline(paging);
        return mTwitterManager.getHomeTimeline();
    }

    public void fetchTimeline(Paging paging) {
        mAppExecutors.networkIO().execute(() -> mTwitterManager.fetchTimeline(paging));
    }

    public MutableLiveData<LoginResult> getLoginResult() {
        return loginResult;
    }
}
