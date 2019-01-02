package com.enos.totalsns.data.source;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Mention;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.source.local.TotalSnsDatabase;
import com.enos.totalsns.data.source.remote.OauthToken;
import com.enos.totalsns.data.source.remote.TwitterManager;
import com.enos.totalsns.intro.LoginResult;
import com.enos.totalsns.util.AppExecutors;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.SingleLiveEvent;
import com.enos.totalsns.util.SingletonToast;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import twitter4j.DirectMessage;
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

    private MediatorLiveData<List<Message>> mObservableDirectMessage;

    private MediatorLiveData<List<Message>> mObservableDirectMessageDetail;

    private MediatorLiveData<List<Mention>> mObservableMention;

    private TwitterManager mTwitterManager;

    private AppExecutors mAppExecutors;
    private TotalSnsDatabase mDatabase;

    private SingleLiveEvent<LoginResult> loginResult;

    private SingleLiveEvent<Boolean> isSnsNetworkOnUse;

    private SingleLiveEvent<Article> currentUploadArticle;

    private SingleLiveEvent<Message> currentUploadDM;

    private SingleLiveEvent<Boolean> isSignOutFinished;

    private static final Object LOCK = new Object();

    private AtomicBoolean mHasSourceAdded = new AtomicBoolean(false);

    private TotalSnsRepository(final TotalSnsDatabase database, final TwitterManager twitterManager) {
        mDatabase = database;
        mTwitterManager = twitterManager;
        mObservableAccounts = new MediatorLiveData<>();
        mObservableTimelines = new MediatorLiveData<>();
        mObservableDirectMessage = new MediatorLiveData<>();
        mObservableDirectMessageDetail = new MediatorLiveData<>();
        mObservableMention = new MediatorLiveData<>();
        mAppExecutors = new AppExecutors();
        loginResult = new SingleLiveEvent<LoginResult>();
        isSnsNetworkOnUse = new SingleLiveEvent<>();
        currentUploadArticle = new SingleLiveEvent<>();
        currentUploadDM = new SingleLiveEvent<>();
        isSignOutFinished = new SingleLiveEvent<>();

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
                    addDirectMessageSource();
                    addMentionSource();
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

    private void addTimelineSource() {

        //리포지토리 생성시에 호출하면 동작안함, 옵저버 추가여부를  확인할수 없어서 예외처리
        try {
            mObservableTimelines.addSource(mDatabase.articleDao().loadArticles(mTwitterManager.getCurrentUserId()),
                    timeline ->
                    {
                        SingletonToast.getInstance().log("timeline", timeline.toString());
                        mObservableTimelines.postValue(timeline);
                    });
        } catch (IllegalArgumentException ignored) {
        }

        try {
            mObservableTimelines.addSource(mTwitterManager.getHomeTimeline(),
                    timeline ->
                    {
                        isSnsNetworkOnUse.postValue(false);
                        SingletonToast.getInstance().log("timeline", timeline.toString());
                        mAppExecutors.diskIO().execute(() -> mDatabase.articleDao().insertArticles(timeline));
                    });
        } catch (IllegalArgumentException ignored) {
        }
    }

    public LiveData<List<Article>> getHomeTimeline() {
//        fetchTimeline(paging);
        return mObservableTimelines;
    }

    public void fetchTimelineForStart(Paging paging) {
        mAppExecutors.diskIO().execute(() -> {
            Article article = mDatabase.articleDao().getLastArticle(mTwitterManager.getCurrentUserId());
            mAppExecutors.networkIO().execute(() -> {
                if (article != null && article.getArticleId() > 0)
                    fetchTimeline(paging.sinceId(article.getArticleId()));
                else fetchTimeline(paging);
            });
        });
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

    public LiveData<Article> getLastArticle() {
        return mDatabase.articleDao().loadLastArticle(mTwitterManager.getCurrentUserId());
    }

    public void fetchRecentTimeline() {
        mAppExecutors.diskIO().execute(() -> {
            Paging paging = new Paging().count(Constants.PAGE_CNT);
            Article last = mDatabase.articleDao().getLastArticle(mTwitterManager.getCurrentUserId());
//            Log.i("timeline", "last : " + last.getMessage());
            paging.sinceId(last.getArticleId());
            fetchTimeline(paging);
        });
    }

    public void fetchPastTimeline() {
        mAppExecutors.diskIO().execute(() -> {
            Paging paging = new Paging().count(Constants.PAGE_CNT);
            Article first = mDatabase.articleDao().getFirstArticle(mTwitterManager.getCurrentUserId());
//            Log.i("timeline", "first : " + first.getMessage());
            paging.setMaxId(first.getArticleId());
            fetchTimeline(paging);
        });
    }

    // start of direct message
    private void addDirectMessageSource() {

        //리포지토리 생성시에 호출하면 동작안함, 옵저버 추가여부를  확인할수 없어서 예외처리
        try {
            mObservableDirectMessage.addSource(mDatabase.messageDao().loadMessages(mTwitterManager.getCurrentUserId()),
                    dmlist ->
                    {
//                        Log.i("timeline", "local : " + timeline.size());
                        mObservableDirectMessage.postValue(dmlist);
                    });
        } catch (IllegalArgumentException ignored) {
        }

        try {
            mObservableDirectMessage.addSource(mTwitterManager.getDirectMessage(),
                    dmlist ->
                    {
                        isSnsNetworkOnUse.postValue(false);
//                        Log.i("timeline", "remote : " + timeline.size());
                        mAppExecutors.diskIO().execute(() -> mDatabase.messageDao().insertMessages(dmlist));
                    });
        } catch (IllegalArgumentException ignored) {
        }
    }

    public LiveData<List<Message>> getDirectMessage() {
//        fetchDirectMessage(count, null);
        return mObservableDirectMessage;
    }

    public synchronized void fetchDirectMessage(int count, String cursor) {
        isSnsNetworkOnUse.postValue(true);

        addDirectMessageSource();

        mAppExecutors.networkIO().execute(() -> {
            try {
                mTwitterManager.fetchDirectMessage(count, cursor);
            } catch (TwitterException e) {
                isSnsNetworkOnUse.postValue(false);
                e.printStackTrace();
            }
        });
    }

    public void fetchRecentDirectMessage() {
        mAppExecutors.diskIO().execute(() -> {
            fetchDirectMessage(Constants.PAGE_CNT, null);
        });
    }

    public void fetchPastDirectMessage() {
        mAppExecutors.diskIO().execute(() -> {
            fetchDirectMessage(Constants.PAGE_CNT, mTwitterManager.getDmCursor());
        });
    }
    // end of direct message

    // start of direct message detail

    private void addDirectMessageSourceDetail(long senderId) {

        //리포지토리 생성시에 호출하면 동작안함, 옵저버 추가여부를  확인할수 없어서 예외처리
        try {
            mObservableDirectMessageDetail.addSource(mDatabase.messageDao().loadMessagesBySenderId(mTwitterManager.getCurrentUserId(), senderId),
                    dmlist ->
                    {
//                        Log.i("timeline", "local : " + timeline.size());
                        mObservableDirectMessageDetail.postValue(dmlist);
                    });
        } catch (IllegalArgumentException ignored) {
        }

        try {
            mObservableDirectMessageDetail.addSource(mTwitterManager.getDirectMessage(),
                    dmlist ->
                    {
                        isSnsNetworkOnUse.postValue(false);
//                        Log.i("timeline", "remote : " + timeline.size());
                        mAppExecutors.diskIO().execute(() -> mDatabase.messageDao().insertMessages(dmlist));
                    });
        } catch (IllegalArgumentException ignored) {
        }
    }

    public LiveData<List<Message>> getDirectMessageDetail() {
        return mObservableDirectMessageDetail;
    }

    public void fetchDirectMessageDetail(long senderTableId) {
        addDirectMessageSourceDetail(senderTableId);
    }
    // end of direct message detail

    // Start of mention
    private void addMentionSource() {

        //리포지토리 생성시에 호출하면 동작안함, 옵저버 추가여부를  확인할수 없어서 예외처리
        try {
            mObservableMention.addSource(mDatabase.mentionDao().loadMentions(mTwitterManager.getCurrentUserId()),
                    timeline ->
                    {
                        SingletonToast.getInstance().log("mention", timeline + "");
                        mObservableMention.postValue(timeline);
                    });
        } catch (IllegalArgumentException ignored) {
        }

        try {
            mObservableMention.addSource(mTwitterManager.getMention(),
                    timeline ->
                    {
                        isSnsNetworkOnUse.postValue(false);
                        SingletonToast.getInstance().log("mention", timeline + "");
                        mAppExecutors.diskIO().execute(() -> mDatabase.mentionDao().insertMentions(timeline));
                    });
        } catch (IllegalArgumentException ignored) {
        }
    }

    public LiveData<List<Mention>> getMention() {
//        fetchMention(paging);
        return mObservableMention;
    }

    public void fetchMentionForStart(Paging paging) {
        mAppExecutors.diskIO().execute(() -> {
            Mention article = mDatabase.mentionDao().getLastMention(mTwitterManager.getCurrentUserId());
            mAppExecutors.networkIO().execute(() -> {
                if (article != null && article.getArticleId() > 0)
                    fetchMention(paging.sinceId(article.getArticleId()));
                else fetchMention(paging);
            });
        });
    }

    public synchronized void fetchMention(Paging paging) {
        isSnsNetworkOnUse.postValue(true);

        addMentionSource();

        mAppExecutors.networkIO().execute(() -> {
            try {
                mTwitterManager.fetchMention(paging);
            } catch (TwitterException e) {
                isSnsNetworkOnUse.postValue(false);
                e.printStackTrace();
            }
        });
    }

    public LiveData<Mention> getLastMention() {
        return mDatabase.mentionDao().loadLastMention(mTwitterManager.getCurrentUserId());
    }

    public void fetchRecentMention() {
        mAppExecutors.diskIO().execute(() -> {
            Paging paging = new Paging().count(Constants.PAGE_CNT);
            Mention last = mDatabase.mentionDao().getLastMention(mTwitterManager.getCurrentUserId());
//            Log.i("timeline", "last : " + last.getMessage());
            if (last != null) paging.sinceId(last.getArticleId());
            fetchMention(paging);
        });
    }

    public void fetchPastMention() {
        mAppExecutors.diskIO().execute(() -> {
            Paging paging = new Paging().count(Constants.PAGE_CNT);
            Mention first = mDatabase.mentionDao().getFirstMention(mTwitterManager.getCurrentUserId());
//            Log.i("timeline", "first : " + first.getMessage());
            paging.setMaxId(first.getArticleId());
            fetchMention(paging);
        });
    }
    // End of mention

    public void signOut() {
        isSignOutFinished.postValue(false);
        mHasSourceAdded.set(false);
        mAppExecutors.networkIO().execute(() -> {
            mObservableTimelines.postValue(null);
            mDatabase.accountDao().updateSignOutBySns(Constants.TWITTER);
            isSnsNetworkOnUse.postValue(true);
            mTwitterManager.signOut();
            isSnsNetworkOnUse.postValue(false);
            isSignOutFinished.postValue(true);
        });
    }

    public LiveData<Boolean> isSignOutFinished() {
        return isSignOutFinished;
    }

    public LiveData<Boolean> isSnsNetworkOnUse() {
        return isSnsNetworkOnUse;
    }

    public SingleLiveEvent<LoginResult> getLoginResult() {
        return loginResult;
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
        Article article = ConvertUtils.toArticle(status, currentUserId);
        isSnsNetworkOnUse.postValue(false);
        mAppExecutors.diskIO().execute(() -> mDatabase.articleDao().insertArticle(article));
        currentUploadArticle.postValue(article);
    }

    public LiveData<Message> getCurrentUploadingDM() {
        return currentUploadDM;
    }

    public void sendDirectMessage(long receiverId, String message, Message userInfo) {
        mAppExecutors.networkIO().execute(() -> {
            try {
                isSnsNetworkOnUse.postValue(true);
                postSendDM(mTwitterManager.sendDirectMessage(receiverId, message, -1), userInfo);
            } catch (TwitterException e) {
                isSnsNetworkOnUse.postValue(false);
                currentUploadDM.postValue(null);
                e.printStackTrace();
            }
        });
    }

    private void postSendDM(DirectMessage dm, Message userInfo) {
        long currentUserId = mTwitterManager.getCurrentUserId();
        Message message = ConvertUtils.toMessage(dm, currentUserId, userInfo);
        isSnsNetworkOnUse.postValue(false);
        mAppExecutors.diskIO().execute(() -> mDatabase.messageDao().insertMessage(message));
        currentUploadDM.postValue(message);
    }
}
