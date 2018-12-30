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
import com.enos.totalsns.util.ConverUtils;
import com.enos.totalsns.util.SingleLiveEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.User;

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

    private SingleLiveEvent<Article> currentUploadArticle;

    private static final Object LOCK = new Object();

    private AtomicBoolean mHasSourceAdded = new AtomicBoolean(false);

    private TotalSnsRepository(final TotalSnsDatabase database, final TwitterManager twitterManager) {
        mDatabase = database;
        mTwitterManager = twitterManager;
        mObservableAccounts = new MediatorLiveData<>();
        mObservableTimelines = new MediatorLiveData<>();
        mAppExecutors = new AppExecutors();
        loginResult = new SingleLiveEvent<LoginResult>();
        isSnsNetworkOnUse = new SingleLiveEvent<>();
        currentUploadArticle = new SingleLiveEvent<>();

        mObservableAccounts.addSource(mDatabase.accountDao().loadAccounts(),
                accounts -> mObservableAccounts.postValue(accounts));
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

    private void addTimelineSource() {

        //리포지토리 생성시에 호출하면 동작안함, 옵저버 추가여부를  확인할수 없어서 예외처리
        try {
            mObservableTimelines.addSource(mDatabase.articleDao().loadArticles(mTwitterManager.getCurrentUserId()),
                    timeline ->
                    {
                        Log.i("timeline", "local : " + timeline.size());
                        mObservableTimelines.postValue(timeline);
                    });
        } catch (IllegalArgumentException ignored) {
        }

        try {
            mObservableTimelines.addSource(mTwitterManager.getHomeTimeline(),
                    timeline ->
                    {
                        isSnsNetworkOnUse.postValue(false);
                        Log.i("timeline", "remote : " + timeline.size());
                        mAppExecutors.diskIO().execute(() -> mDatabase.articleDao().insertArticles(timeline));
                    });
        } catch (IllegalArgumentException ignored) {
        }
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
                if (mHasSourceAdded.compareAndSet(false, true)) {
                    addTimelineSource();
                }

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
        mHasSourceAdded.set(false);
        mAppExecutors.networkIO().execute(() -> {
            mObservableTimelines.postValue(null);
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

        addTimelineSource();

        mAppExecutors.networkIO().execute(() -> {
            try {
                mTwitterManager.fetchTimeline(paging);
            } catch (TwitterException e) {
                isSnsNetworkOnUse.postValue(false);
                e.printStackTrace();
            }
        });
    }

    public SingleLiveEvent<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> isSnsNetworkOnUse() {
        return isSnsNetworkOnUse;
    }

    public LiveData<Article> getLastArticle() {
        return mDatabase.articleDao().loadLastArticle(mTwitterManager.getCurrentUserId());
    }

    public void fetchRecentTimeline() {
        mAppExecutors.diskIO().execute(() -> {
            Paging paging = new Paging().count(Constants.PAGE_CNT);
            Article last = mDatabase.articleDao().getLastArticle(mTwitterManager.getCurrentUserId());
            Log.i("timeline", "last : " + last.getMessage());
            paging.sinceId(last.getArticleId());
            fetchTimeline(paging);
        });
    }

    public void fetchPastTimeline() {
        mAppExecutors.diskIO().execute(() -> {
            Paging paging = new Paging().count(Constants.PAGE_CNT);
            Article first = mDatabase.articleDao().getFirstArticle(mTwitterManager.getCurrentUserId());
            Log.i("timeline", "first : " + first.getMessage());
            paging.setMaxId(first.getArticleId());
            fetchTimeline(paging);
        });
    }

    public LiveData<User> getLoggedInUser() {
        mAppExecutors.networkIO().execute(() -> {
            try {
                isSnsNetworkOnUse.postValue(true);
                mTwitterManager.fetchLoggedInUser();
                isSnsNetworkOnUse.postValue(false);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
        return mTwitterManager.getLoggedInUser();
    }

    public LiveData<Article> getCurrentUploadingArticle() {
        return currentUploadArticle;
    }

    public void uploadStatus(String message) {
        mAppExecutors.networkIO().execute(() -> {
            try {
                isSnsNetworkOnUse.postValue(true);
                postUploadStatus(mTwitterManager.updateStatus(message));
            } catch (TwitterException e) {
                isSnsNetworkOnUse.postValue(false);
                currentUploadArticle.postValue(null);
                e.printStackTrace();
            }
        });
    }

    public void uploadStatus(StatusUpdate statusUpdate) {
        mAppExecutors.networkIO().execute(() -> {
            try {
                postUploadStatus(mTwitterManager.updateStatus(statusUpdate));
            } catch (TwitterException e) {
                isSnsNetworkOnUse.postValue(false);
                currentUploadArticle.postValue(null);
                e.printStackTrace();
            }
        });
    }

    private void postUploadStatus(Status status) {
        long currentUserId = mTwitterManager.getCurrentUserId();
        Article article = ConverUtils.toArticle(status, currentUserId);
        isSnsNetworkOnUse.postValue(false);
        mAppExecutors.diskIO().execute(() -> mDatabase.articleDao().insertArticle(article));
        currentUploadArticle.postValue(article);
    }

    public SingleLiveEvent<Article> getCurrentUploadArticle() {
        return currentUploadArticle;
    }

}
