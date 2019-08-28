package com.enos.totalsns.data.source;


import com.enos.totalsns.AppExecutors;
import com.enos.totalsns.custom.SingleLiveEvent;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.local.TotalSnsDatabase;
import com.enos.totalsns.data.source.remote.OauthToken;
import com.enos.totalsns.data.source.remote.QueryArticleNearBy;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.data.source.remote.QueryMention;
import com.enos.totalsns.data.source.remote.QueryMessage;
import com.enos.totalsns.data.source.remote.QuerySearchArticle;
import com.enos.totalsns.data.source.remote.QuerySearchUser;
import com.enos.totalsns.data.source.remote.QueryTimeline;
import com.enos.totalsns.data.source.remote.QueryUploadArticle;
import com.enos.totalsns.data.source.remote.QueryUploadMessage;
import com.enos.totalsns.data.source.remote.QueryUserTimeline;
import com.enos.totalsns.data.source.remote.TwitterManager;
import com.enos.totalsns.intro.LoginResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.collection.LongSparseArray;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.TwitterException;

/**
 * Repository handling the work with products and comments.
 */
public class TotalSnsRepository {

    //TODO SNS Client Repository 생성 및 SNS별 기능 추가
    private static TotalSnsRepository sInstance;

    private TwitterManager mTwitterManager;

    private AppExecutors mAppExecutors;

    private TotalSnsDatabase mDatabase;

    private static final Object LOCK = new Object();

    private AtomicBoolean mHasSourceAdded = new AtomicBoolean(false);

    private MediatorLiveData<List<Article>> mObservableTimelines;

    private MediatorLiveData<List<Message>> mObservableDirectMessage;

    private MediatorLiveData<List<Article>> mObservableMention;

    private MutableLiveData<List<Article>> mObservableSearch;

    private MutableLiveData<List<UserInfo>> mObservableSearchUser;

    private SingleLiveEvent<LoginResult> loginResult;

    private SingleLiveEvent<Boolean> isSnsNetworkOnUse;

    private SingleLiveEvent<Article> currentUploadArticle;

    private SingleLiveEvent<Message> currentUploadDM;

    private SingleLiveEvent<Boolean> isSignOutFinished;

    private SingleLiveEvent<String> mSearchQuery;

    private MutableLiveData<UserInfo> loggedInUser;

    private MutableLiveData<List<Article>> nearbyArticle;

    private MutableLiveData<LongSparseArray<UserInfo>> userCache;

    private TotalSnsRepository(final TotalSnsDatabase database, final TwitterManager twitterManager) {
        mDatabase = database;
        mTwitterManager = twitterManager;
        mAppExecutors = new AppExecutors();
        mObservableTimelines = new MediatorLiveData<>();
        mObservableDirectMessage = new MediatorLiveData<>();
        mObservableMention = new MediatorLiveData<>();
        mObservableSearch = new MutableLiveData<>();
        mObservableSearchUser = new MutableLiveData<>();
        loginResult = new SingleLiveEvent<LoginResult>();
        isSnsNetworkOnUse = new SingleLiveEvent<>();
        currentUploadArticle = new SingleLiveEvent<>();
        currentUploadDM = new SingleLiveEvent<>();
        isSignOutFinished = new SingleLiveEvent<>();
        mSearchQuery = new SingleLiveEvent<>();
        loggedInUser = new MutableLiveData<>();
        nearbyArticle = new MutableLiveData<>();
        userCache = new MutableLiveData<>();
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

    public LiveData<List<Account>> getAccounts() {
        return mDatabase.accountDao().loadAccounts();
    }

    public void addUserToCache(UserInfo user) {
        if (user == null) return;
        LongSparseArray<UserInfo> users = userCache.getValue();
        if (users == null) users = new LongSparseArray<>();
        users.put(user.getLongUserId(), user);
        userCache.postValue(users);
    }

    public void addUserListToCache(List<UserInfo> users) {
        if (users == null) return;
        LongSparseArray<UserInfo> current = userCache.getValue();
        if (current == null) current = new LongSparseArray<>();
        for (UserInfo user : users) {
            current.put(user.getLongUserId(), user);
        }
        userCache.postValue(current);
    }

    public UserInfo getUserFromCache(long userId) {
        LongSparseArray<UserInfo> users = userCache.getValue();
        return users.get(userId);
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
                isSnsNetworkOnUse.postValue(false);
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

    public synchronized void signInTwitterWithOauthToken(OauthToken token, boolean isEnableUpdate) {
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
                    UserInfo user = mTwitterManager.getLoggedInUser();
                    addUserToCache(user);
                    addTimelineSource(user.getLongUserId());
                    addDirectMessageSource(user.getLongUserId());
                    addMentionSource(user.getLongUserId());
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

    private void addTimelineSource(long currentUser) {

        //리포지토리 생성시에 호출하면 동작안함, 옵저버 추가여부를  확인할수 없어서 IllegalArgumentException 예외처리
        try {
            mObservableTimelines.addSource(mDatabase.articleDao().loadArticles(currentUser),
                    timeline ->
                    {
                        mObservableTimelines.postValue(timeline);
                    });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            mObservableTimelines.addSource(mTwitterManager.getHomeTimeline(),
                    timeline ->
                    {
                        mAppExecutors.diskIO().execute(() -> mDatabase.articleDao().insertArticles(timeline));
                    });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public MutableLiveData<List<Article>> getHomeTimeline() {
        return mObservableTimelines;
    }

    public synchronized void fetchTimeline(QueryTimeline query) {
        isSnsNetworkOnUse.postValue(true);

        mAppExecutors.diskIO().execute(() -> {
            try {
                long userId = mTwitterManager.getLoggedInUser().getLongUserId();
                mAppExecutors.mainThread().execute(() -> addTimelineSource(userId));

                Paging paging = new Paging().count(Constants.PAGE_CNT);
                switch (query.getQueryType()) {
                    case QueryTimeline.FIRST:
                    case QueryTimeline.RECENT:
                        Article last = mDatabase.articleDao().getLastArticle(userId);
                        if (last != null && last.getArticleId() > 0)
                            paging.setSinceId(last.getArticleId());
                        break;
                    case QueryTimeline.PAST:
                        Article first = mDatabase.articleDao().getFirstArticle(userId);
                        if (first != null && first.getArticleId() > 0)
                            paging.setMaxId(first.getArticleId() - 1);
                        break;
                }

                mAppExecutors.networkIO().execute(() -> {
                    try {
                        mTwitterManager.fetchTimeline(paging);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    isSnsNetworkOnUse.postValue(false);
                });
            } catch (TwitterException e) {
                e.printStackTrace();
                isSnsNetworkOnUse.postValue(false);
            }
        });
    }

    // start of direct message
    private void addDirectMessageSource(long userId) {

        //리포지토리 생성시에 호출하면 동작안함, 옵저버 추가여부를  확인할수 없어서 예외처리
        try {
            mObservableDirectMessage.addSource(mDatabase.messageDao().loadMessageList(userId),
                    dmlist ->
                    {
                        mObservableDirectMessage.postValue(dmlist);
                    });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            mObservableDirectMessage.addSource(mTwitterManager.getDirectMessage(),
                    dmlist ->
                    {
                        mAppExecutors.diskIO().execute(() -> mDatabase.messageDao().insertMessages(dmlist));
                    });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public MutableLiveData<List<Message>> getDirectMessage() {
        return mObservableDirectMessage;
    }

    public synchronized void fetchDirectMessage(QueryMessage message) {
        isSnsNetworkOnUse.postValue(true);

        mAppExecutors.diskIO().execute(() -> {
            try {
                long userId = mTwitterManager.getLoggedInUser().getLongUserId();
                mAppExecutors.mainThread().execute(() -> addDirectMessageSource(userId));
                mAppExecutors.networkIO().execute(() -> {
                    try {
                        mTwitterManager.fetchDirectMessage(message);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    isSnsNetworkOnUse.postValue(false);
                });
            } catch (TwitterException e) {
                isSnsNetworkOnUse.postValue(false);
                e.printStackTrace();
            }
        });
    }

    private void addDirectMessageSourceDetail(long userId, long otherId, MediatorLiveData<List<Message>> liveData) {

        //리포지토리 생성시에 호출하면 동작안함, 옵저버 추가여부를  확인할수 없어서 예외처리
        try {
            liveData.addSource(mDatabase.messageDao().loadMessagesBySenderId(userId, otherId),
                    dmlist -> liveData.postValue(dmlist));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            liveData.addSource(mTwitterManager.getDirectMessage(),
                    dmlist -> mAppExecutors.diskIO().execute(() -> mDatabase.messageDao().insertMessages(dmlist)));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void fetchDirectMessageDetail(long senderTableId, MediatorLiveData<List<Message>> liveData) {
        mAppExecutors.diskIO().execute(() -> {
            try {
                long userId = mTwitterManager.getLoggedInUser().getLongUserId();
                mAppExecutors.mainThread().execute(() -> addDirectMessageSourceDetail(userId, senderTableId, liveData));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    private void addMentionSource(long userId) {

        //리포지토리 생성시에 호출하면 동작안함, 옵저버 추가여부를  확인할수 없어서 예외처리
        try {
            mObservableMention.addSource(mDatabase.articleDao().loadMentions(userId),
                    timeline ->
                    {
                        mObservableMention.postValue(timeline);
                    });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            mObservableMention.addSource(mTwitterManager.getMention(),
                    timeline ->
                    {
                        mAppExecutors.diskIO().execute(() -> mDatabase.articleDao().insertArticles(timeline));
                    });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public MutableLiveData<List<Article>> getMention() {
        return mObservableMention;
    }

    public synchronized void fetchMention(QueryMention query) {
        isSnsNetworkOnUse.postValue(true);

        mAppExecutors.diskIO().execute(() -> {
            try {
                long userId = mTwitterManager.getLoggedInUser().getLongUserId();
                mAppExecutors.mainThread().execute(() -> addMentionSource(userId));

                Paging paging = new Paging().count(Constants.PAGE_CNT);
                switch (query.getQueryType()) {
                    case QueryMention.FIRST:
                    case QueryMention.RECENT:
                        Article last = mDatabase.articleDao().getLastMention(userId);
                        if (last != null && last.getArticleId() > 0)
                            paging.setSinceId(last.getArticleId());
                        break;
                    case QueryMention.PAST:
                        Article first = mDatabase.articleDao().getFirstMention(userId);
                        if (first != null && first.getArticleId() > 0)
                            paging.setMaxId(first.getArticleId() - 1);
                        break;
                }

                mAppExecutors.networkIO().execute(() -> {
                    try {
                        mTwitterManager.fetchMention(paging);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    isSnsNetworkOnUse.postValue(false);
                });
            } catch (TwitterException e) {
                isSnsNetworkOnUse.postValue(false);
                e.printStackTrace();
            }
        });
    }

    public synchronized void signOut() {
        isSignOutFinished.postValue(false);
        mHasSourceAdded.set(false);
        mAppExecutors.networkIO().execute(() -> {
            mObservableTimelines.postValue(null);
            mObservableMention.postValue(null);
            mObservableDirectMessage.postValue(null);
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

    public LiveData<UserInfo> getLoggedInUser() {
        isSnsNetworkOnUse.postValue(true);
        mAppExecutors.networkIO().execute(() -> {
            try {
                UserInfo user = mTwitterManager.getLoggedInUser();
                loggedInUser.postValue(user);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            isSnsNetworkOnUse.postValue(false);
        });
        return loggedInUser;
    }

    public MutableLiveData<Article> getCurrentUploadingArticle() {
        return currentUploadArticle;
    }

    public void postArticle(String message) {
        postArticle(new QueryUploadArticle(message));
    }

    public synchronized void postArticle(QueryUploadArticle query) {
        isSnsNetworkOnUse.postValue(true);
        mAppExecutors.networkIO().execute(() -> {
            Article article = null;
            try {
                article = mTwitterManager.updateStatus(query);
                final Article insert = article;
                mAppExecutors.diskIO().execute(() -> mDatabase.articleDao().insertArticle(insert));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            isSnsNetworkOnUse.postValue(false);
            currentUploadArticle.postValue(article);
        });
    }

    public MutableLiveData<Message> getCurrentUploadingDM() {
        return currentUploadDM;
    }

    public synchronized void sendDirectMessage(QueryUploadMessage query) {
        isSnsNetworkOnUse.postValue(true);
        mAppExecutors.networkIO().execute(() -> {
            Message message = null;
            try {
                message = mTwitterManager.sendDirectMessage(query);
                final Message msgFinal = message;
                mAppExecutors.diskIO().execute(() -> mDatabase.messageDao().insertMessage(msgFinal));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            isSnsNetworkOnUse.postValue(false);
            currentUploadDM.postValue(message);
        });
    }

    // start of search
    public synchronized void fetchSearchTotal(Query count) {
        isSnsNetworkOnUse.postValue(true);

        mAppExecutors.networkIO().execute(() -> {
            ArrayList<Article> articles = null;
            ArrayList<UserInfo> users = null;
            try {
                articles = mTwitterManager.getSearch(new QuerySearchArticle(QuerySearchArticle.FIRST, count.getQuery()));
                mObservableSearch.postValue(articles);
                users = mTwitterManager.getSearchUser(new QuerySearchUser(QuerySearchUser.FIRST, count.getQuery()));
                addUserListToCache(users);
                mObservableSearchUser.postValue(users);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            isSnsNetworkOnUse.postValue(false);
        });
    }

    public MutableLiveData<List<Article>> getSearchList() {
        return mObservableSearch;
    }

    public synchronized void fetchSearch(QuerySearchArticle query) {
        isSnsNetworkOnUse.postValue(true);

        mAppExecutors.networkIO().execute(() -> {
            try {
                ArrayList<Article> articles = mTwitterManager.getSearch(query);
                mObservableSearch.postValue(articles);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            isSnsNetworkOnUse.postValue(false);
        });
    }

    public SingleLiveEvent<String> getSearchQuery() {
        return mSearchQuery;
    }

    public MutableLiveData<List<UserInfo>> getSearchUserList() {
        return mObservableSearchUser;
    }

    public synchronized void fetchSearchUser(QuerySearchUser query, MutableLiveData<List<UserInfo>> liveData) {
        isSnsNetworkOnUse.postValue(true);

        mAppExecutors.networkIO().execute(() -> {
            ArrayList<UserInfo> list = null;
            try {
                list = mTwitterManager.getSearchUser(query);
                addUserListToCache(list);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            isSnsNetworkOnUse.postValue(false);
            liveData.postValue(list);
        });
    }

    public void addSearchUserMoreSource(MediatorLiveData<List<UserInfo>> liveData) {
        try {
            liveData.addSource(mObservableSearchUser,
                    searchList ->
                    {
                        liveData.removeSource(mObservableSearchUser);
                        liveData.postValue(searchList);
                    }
            );
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public synchronized void fetchSearchUserMore(QuerySearchUser query, MutableLiveData<List<UserInfo>> liveData) {
        isSnsNetworkOnUse.postValue(true);

        mAppExecutors.networkIO().execute(() -> {
            ArrayList<UserInfo> list = null;
            try {
                list = mTwitterManager.getSearchUser(query);
                addUserListToCache(list);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            isSnsNetworkOnUse.postValue(false);
            liveData.postValue(list);
        });
    }

    public synchronized void fetchProfile(long userId, MutableLiveData<UserInfo> userProfile) {
        if (getUserFromCache(userId) != null) userProfile.postValue(getUserFromCache(userId));
        isSnsNetworkOnUse.postValue(true);
        mAppExecutors.networkIO().execute(() -> {
            try {
                UserInfo userInfo = mTwitterManager.getUserInfo(userId);
                addUserToCache(userInfo);
                userProfile.postValue(userInfo);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            isSnsNetworkOnUse.postValue(false);
        });
    }

    public synchronized void fetchFollowList(QueryFollow queryFollow, MutableLiveData<List<UserInfo>> followList) {
        isSnsNetworkOnUse.postValue(true);
        mAppExecutors.networkIO().execute(() -> {
            ArrayList<UserInfo> followerList = null;
            try {
                followerList = mTwitterManager.getFollowList(queryFollow);
                addUserListToCache(followerList);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            isSnsNetworkOnUse.postValue(false);
            followList.postValue(followerList);
        });
    }

    public MutableLiveData<List<Article>> getNearbySearchList() {
        return nearbyArticle;
    }

    public void fetchNearbySearch(QueryArticleNearBy query) {
        isSnsNetworkOnUse.postValue(true);
        mAppExecutors.networkIO().execute(() -> {
            ArrayList<Article> nearbyList = null;
            try {
                nearbyList = mTwitterManager.getSearchNearBy(query);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            isSnsNetworkOnUse.postValue(false);
            nearbyArticle.postValue(nearbyList);
        });
    }

    public void fetchUserTimeline(QueryUserTimeline query, MutableLiveData<List<Article>> userTimeline) {
        isSnsNetworkOnUse.postValue(true);

        mAppExecutors.networkIO().execute(() -> {
            ArrayList<Article> articles = null;
            try {
                articles = mTwitterManager.getUserTimeline(query);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            isSnsNetworkOnUse.postValue(false);
            userTimeline.postValue(articles);
        });
    }

    public void fetchFollow(long id, boolean isFollow, MutableLiveData<UserInfo> liveData) {
        isSnsNetworkOnUse.postValue(true);

        mAppExecutors.networkIO().execute(() -> {
            UserInfo user = null;
            try {
                user = isFollow ? mTwitterManager.followUser(id) : mTwitterManager.unfollowUser(id);
                addUserToCache(user);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            liveData.postValue(user);
            isSnsNetworkOnUse.postValue(false);
        });
    }

    public LiveData<LongSparseArray<UserInfo>> getUserCache() {
        return userCache;
    }
}