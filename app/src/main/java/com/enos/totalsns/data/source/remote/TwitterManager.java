package com.enos.totalsns.data.source.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.enos.totalsns.BuildConfig;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.SingletonToast;

import java.util.ArrayList;

import twitter4j.DirectMessage;
import twitter4j.DirectMessageList;
import twitter4j.GeoQuery;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager {

    public static final long INVALID_ID = -1;
    private static TwitterManager mTwitterManager = null;

    private Twitter mTwitter = null;

    private MutableLiveData<ArrayList<Article>> homeTimeline;

    private MutableLiveData<ArrayList<Message>> directMessage;

    private MutableLiveData<ArrayList<Article>> mentionList;

    private MutableLiveData<User> loggedInUser;

    private TwitterManager() {
        init();
    }

    private long currentUserId = INVALID_ID;

    private String mDmCursor = null;

    public static TwitterManager getInstance() {
        if (mTwitterManager == null) {
            mTwitterManager = new TwitterManager();
//            Log.i("tweet", "tweet constructor called");
        }
        return mTwitterManager;
    }

    public void init() {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(Constants.IS_DEBUG)
                .setOAuthConsumerKey(BuildConfig.CONSUMER_KEY)
                .setOAuthConsumerSecret(BuildConfig.CONSUMER_SECRET)
                .setIncludeEmailEnabled(true);

        TwitterFactory tf = new TwitterFactory(cb.build());
        mTwitter = tf.getInstance();
        homeTimeline = new MutableLiveData<ArrayList<Article>>();
        directMessage = new MutableLiveData<ArrayList<Message>>();
        mentionList = new MutableLiveData<ArrayList<Article>>();
        loggedInUser = new MutableLiveData<>();
    }

    public String getAuthorizationUrl() throws TwitterException {
        if (mTwitter == null) return null;
        return mTwitter.getOAuthRequestToken().getAuthorizationURL();
    }

    public Account signInWithOauthToken(OauthToken token) throws TwitterException {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(Constants.IS_DEBUG)
                .setOAuthConsumerKey(BuildConfig.CONSUMER_KEY)
                .setOAuthConsumerSecret(BuildConfig.CONSUMER_SECRET)
                .setOAuthAccessToken(token.getToken())
                .setOAuthAccessTokenSecret(token.getSecret())
                .setIncludeEmailEnabled(true);
        TwitterFactory tf = new TwitterFactory(cb.build());
        mTwitter = tf.getInstance();

        if (!token.isAccessToken()) {
            AccessToken accessToken = mTwitter.getOAuthAccessToken(token.getSecret());
            token.setToken(accessToken.getToken());
            token.setSecret(accessToken.getTokenSecret());
        }

        User credential = mTwitter.verifyCredentials();
        long userId = credential.getId();
        String profileImg = credential.get400x400ProfileImageURL();
        String screenName = credential.getScreenName();
        String name = credential.getName();
        currentUserId = userId;

        return new Account(userId, screenName, token.getToken(), token.getSecret(), profileImg, name, Constants.TWITTER, true);
    }

    public Status updateStatus(String message) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.updateStatus(message);
    }

    public Status updateStatus(StatusUpdate status) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.updateStatus(status);
    }

    // Start of home timeline
    public ResponseList<Status> getTimeLine(Paging paging) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.getHomeTimeline(paging);
    }

    public LiveData<ArrayList<Article>> getHomeTimeline() {
        return homeTimeline;
    }

    public void fetchTimeline(Paging paging) throws TwitterException {

        ResponseList<Status> list = getTimeLine(paging);
        ArrayList<Article> articleList = new ArrayList<Article>();
        long currentUserId = getCurrentUserId();
        int num = 0;
        for (Status status : list) {
            num++;
            SingletonToast.getInstance().log("timeline", status.toString());
            Article article = ConvertUtils.toArticle(status, currentUserId);
            articleList.add(article);
        }
        homeTimeline.postValue(articleList);
    }
    // End of home timeline

    // Start of mention timeline
    public ResponseList<Status> getMention(Paging paging) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.getMentionsTimeline(paging);
    }

    public LiveData<ArrayList<Article>> getMention() {
        return mentionList;
    }

    public void fetchMention(Paging paging) throws TwitterException {

        ResponseList<Status> list = getMention(paging);
        SingletonToast.getInstance().log("mention", list + "");
        ArrayList<Article> articleList = new ArrayList<Article>();
        long currentUserId = getCurrentUserId();
        int num = 0;
        for (Status status : list) {
            num++;
//            Log.i("timeline", num + ":" + status.getText());
            SingletonToast.getInstance().log("mention", status + "");
            Article mention = ConvertUtils.toMention(status, currentUserId);
            articleList.add(mention);
        }
        mentionList.postValue(articleList);
    }
    // End of mention timeline

    // Start of User timeline
    public ResponseList<Status> getUserTimeline(long userId, Paging paging) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.getUserTimeline(userId, paging);
    }
    // End of User timeline

    // Start of Direct Message
    public String getDmCursor() {
        return mDmCursor;
    }

    public LiveData<ArrayList<Message>> getDirectMessage() {
        return directMessage;
    }

    public void fetchDirectMessage(int count, String cursor) throws TwitterException {

        DirectMessageList list = getDirectMessage(count, cursor);

        mDmCursor = list.getNextCursor();

        long[] userSet = ConvertUtils.getUserIdSet(list, getCurrentUserId());

        ResponseList<User> userList = mTwitter.lookupUsers(userSet);

        directMessage.postValue(ConvertUtils.toMessageList(list, getCurrentUserId(), ConvertUtils.getUserIdMap(userList)));
    }

    public DirectMessageList getDirectMessage(int count, String cursor) throws TwitterException {
        if (mTwitter == null) return null;
        return (cursor == null || cursor.length() <= 0) ? mTwitter.getDirectMessages(count) :
                mTwitter.getDirectMessages(count, cursor);
    }
    // End of Direct Message

    public DirectMessage sendDirectMessage(long userId, String message, long mediaId) throws TwitterException {
        if (mTwitter == null) return null;
        return mediaId <= 0 ? mTwitter.sendDirectMessage(userId, message) : mTwitter.sendDirectMessage(userId, message, mediaId);
    }

    public QueryResult search(Query query) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.search(query);
    }

    public ResponseList<Place> search(GeoQuery geoQuery) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.searchPlaces(geoQuery);
    }

    public ResponseList<User> searchUser(String query, int page) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.searchUsers(query, page);
    }

    public void signOut() {
        mTwitter = TwitterFactory.getSingleton();
    }

    public LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }

    public void fetchLoggedInUser() throws TwitterException {
        User user = mTwitter.verifyCredentials();
        loggedInUser.postValue(user);
    }

    public long getCurrentUserId() throws RuntimeException {
        if (currentUserId <= INVALID_ID) throw new RuntimeException("twitter id is invalid");
        return currentUserId;
    }
}
