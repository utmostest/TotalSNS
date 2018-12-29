package com.enos.totalsns.data.source;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.source.local.TotalSnsDatabase;
import com.enos.totalsns.data.source.remote.OauthToken;
import com.enos.totalsns.data.source.remote.TwitterManager;
import com.enos.totalsns.intro.LoginResult;
import com.enos.totalsns.util.AppExecutors;
import com.enos.totalsns.util.SingleLiveEvent;

import java.util.List;

import twitter4j.Paging;
import twitter4j.TwitterException;

/**
 * Repository handling the work with products and comments.
 */
public class TotalSnsRepository {

    //TODO SNS Client Repository 생성 및 SNS별 기능 추가
    private static TotalSnsRepository sInstance;

    private MediatorLiveData<List<Account>> mObservableAccounts;

    private MediatorLiveData<List<Article>> mObservableTimelines;

    private TwitterManager mTwitterManager;

    private AppExecutors mAppExecutors;
    private TotalSnsDatabase mDatabase;

    private SingleLiveEvent<LoginResult> loginResult;

    private SingleLiveEvent<Boolean> isSnsNetworkOnUse;

    private static final Object LOCK = new Object();

    private TotalSnsRepository(final TotalSnsDatabase database, final TwitterManager twitterManager) {
        mDatabase = database;
        mTwitterManager = twitterManager;
        mObservableAccounts = new MediatorLiveData<>();
        mObservableTimelines = new MediatorLiveData<>();
        mAppExecutors = new AppExecutors();
        loginResult = new SingleLiveEvent<LoginResult>();
        isSnsNetworkOnUse = new SingleLiveEvent<>();

        mObservableAccounts.addSource(mDatabase.accountDao().loadAccounts(),
                accounts -> mObservableAccounts.postValue(accounts));

        mObservableTimelines.addSource(mDatabase.articleDao().loadArticles(),
                timeline ->
                {
                    Log.i("timeline", timeline.size() + "");
                    mObservableTimelines.postValue(timeline);
                });
    }

    public synchronized static TotalSnsRepository getInstance(final TotalSnsDatabase database, final TwitterManager twitterManager) {
        if (sInstance == null) {
            synchronized (LOCK) {
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
            isSnsNetworkOnUse.postValue(true);
            mTwitterManager.init();
            LoginResult oldResult = loginResult.getValue();
            if (oldResult == null) oldResult = new LoginResult();
            oldResult.setLoginStep(LoginResult.STEP1_INIT);
            String url = null;
            try {
                url = mTwitterManager.getAuthorizationUrl();
                isSnsNetworkOnUse.postValue(false);
                oldResult.setLoginStatus(LoginResult.STATUS_LOGIN_SUCCEED);
                oldResult.setAuthorizationUrl(url);
                loginResult.postValue(oldResult);
            } catch (TwitterException e) {
                isSnsNetworkOnUse.postValue(false);
                oldResult.setLoginStatus(LoginResult.STATUS_LOGIN_FAILED);
                oldResult.setMessage(e.getMessage());
                loginResult.postValue(oldResult);
            }
        });
    }

    public void signInTwitterWithSaved(boolean isEnableUpdate) {
        mAppExecutors.networkIO().execute(() -> {
            isSnsNetworkOnUse.postValue(true);
            mTwitterManager.init();
            Account current = mDatabase.accountDao().getCurrentAccountsBySns(Constants.TWITTER);
            if (current != null) {
                OauthToken oauthToken = new OauthToken(current.getOauthKey(), current.getOauthSecret());
                oauthToken.setIsAccessToken(true);
                signInTwitterWithOauthToken(oauthToken, isEnableUpdate);
            } else {
                isSnsNetworkOnUse.postValue(false);
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
        isSnsNetworkOnUse.postValue(true);
        mAppExecutors.networkIO().execute(() -> {
            LoginResult oldResult = loginResult.getValue();
            if (oldResult == null) oldResult = new LoginResult();
            oldResult.setLoginStep(LoginResult.STEP3_ENTIRELOGIN);

            try {
                if (token == null || token.getToken() == null || token.getSecret() == null) {
                    isSnsNetworkOnUse.postValue(false);
                    oldResult.setMessage("oauth isn't valid");
                    loginResult.postValue(oldResult);
                    return;
                }

                Account account = mTwitterManager.signInWithOauthToken(token);
                if (isEnableUpdate) {
                    mAppExecutors.diskIO().execute(() -> mDatabase.updateCurrentUser(account, Constants.TWITTER));
                }
                isSnsNetworkOnUse.postValue(false);
                oldResult.setLoginStatus(LoginResult.STATUS_LOGIN_SUCCEED);
                loginResult.postValue(oldResult);
                return;

            } catch (TwitterException e) {
                isSnsNetworkOnUse.postValue(false);
                oldResult.setLoginStatus(LoginResult.STATUS_LOGIN_FAILED);
                oldResult.setMessage(e.getMessage());
                loginResult.postValue(oldResult);
            }
        });
    }

    public void signOut() {
        mAppExecutors.networkIO().execute(() -> {
            mDatabase.accountDao().updateSignOutBySns(Constants.TWITTER);
            isSnsNetworkOnUse.postValue(true);
            mTwitterManager.signOut();
            isSnsNetworkOnUse.postValue(false);
        });
    }

    public LiveData<List<Article>> getHomeTimeline(Paging paging) {
        fetchTimeline(paging);
        return mObservableTimelines;
    }

    public synchronized void fetchTimeline(Paging paging) {
        isSnsNetworkOnUse.postValue(true);
        try {
            mObservableTimelines.addSource(mTwitterManager.getHomeTimeline(),
                    timeline ->
                    {
                        isSnsNetworkOnUse.postValue(false);
                        Log.i("timeline", timeline.size() + "");
                        mAppExecutors.diskIO().execute(() -> mDatabase.articleDao().insertArticles(timeline));
                    });
            //리포지토리 생성시에 호출하면 동작안함, 옵저버 추가여부를  확인할수 없어서 예외처리
        } catch (IllegalArgumentException ignored) {
        }

        mAppExecutors.networkIO().execute(() -> {
            try {
                mTwitterManager.fetchTimeline(paging);
            } catch (TwitterException e) {
                isSnsNetworkOnUse.postValue(false);
                e.printStackTrace();
            }
        });
    }

    public MutableLiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> isSnsNetworkOnUse() {
        return isSnsNetworkOnUse;
    }

    public LiveData<Article> getLastArticle() {
        return mDatabase.articleDao().loadLastArticle();
    }

    public void fetchRecentTimeline() {
        mAppExecutors.diskIO().execute(() -> {
            Paging paging = new Paging().count(Constants.PAGE_CNT);
            Article last = mDatabase.articleDao().getLastArticle();
            Log.i("timeline", last.getMessage());
            paging.sinceId(last.getArticleId());
            fetchTimeline(paging);
        });
    }

    public void fetchPastTimeline() {
        mAppExecutors.diskIO().execute(() -> {
            Paging paging = new Paging().count(Constants.PAGE_CNT);
            Article first = mDatabase.articleDao().getFirstArticle();
            Log.i("timeline", first.getMessage());
            paging.setMaxId(first.getArticleId());
            fetchTimeline(paging);
        });
    }
}
