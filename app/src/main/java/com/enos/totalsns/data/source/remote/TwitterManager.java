package com.enos.totalsns.data.source.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.enos.totalsns.BuildConfig;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.SingletonToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import twitter4j.DirectMessage;
import twitter4j.DirectMessageList;
import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.PagableResponseList;
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
    private MutableLiveData<ArrayList<Article>> searchList;
    private MutableLiveData<ArrayList<UserInfo>> searchUserList;

    private UserInfo loggedInUser;

    private MutableLiveData<UserInfo> userProfile;

    private QuerySearchArticle mQuery;

    private QueryArticleNearBy mQueryNearBy;

    private QuerySearchUser mUserQuery;

    private QueryFollow queryFollow;

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
        cb.setDebugEnabled(Constants.IS_TWITTER_DEBUG)
                .setOAuthConsumerKey(BuildConfig.CONSUMER_KEY)
                .setOAuthConsumerSecret(BuildConfig.CONSUMER_SECRET)
                .setIncludeEmailEnabled(true);

        TwitterFactory tf = new TwitterFactory(cb.build());
        mTwitter = tf.getInstance();
        homeTimeline = new MutableLiveData<ArrayList<Article>>();
        directMessage = new MutableLiveData<ArrayList<Message>>();
        mentionList = new MutableLiveData<ArrayList<Article>>();
        searchList = new MutableLiveData<ArrayList<Article>>();
        searchUserList = new MutableLiveData<ArrayList<UserInfo>>();
        userProfile = new MutableLiveData<>();

        initSearchVariable();
    }

    public String getAuthorizationUrl() throws TwitterException {
        if (mTwitter == null) return null;
        return mTwitter.getOAuthRequestToken().getAuthorizationURL();
    }

    public Account signInWithOauthToken(OauthToken token) throws TwitterException {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(Constants.IS_TWITTER_DEBUG)
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
        currentUserId = userId;
        String profileImg = credential.get400x400ProfileImageURL();
        String screenName = credential.getScreenName();
        String name = credential.getName();

        loggedInUser = ConvertUtils.toUserInfo(credential, userId);

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

    // Start of search
    private void initSearchVariable() {
        if (mQuery == null) mQuery = new QuerySearchArticle(QuerySearchArticle.FIRST, "");
        if (mUserQuery == null) mUserQuery = new QuerySearchUser(QuerySearchUser.FIRST, "");
        if (queryFollow == null) queryFollow = new QueryFollow(QueryFollow.FIRST, 0, -1, false);
        if (mQueryNearBy == null)
            mQueryNearBy = new QueryArticleNearBy(QueryArticleNearBy.FIRST, Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE);
    }

    public QuerySearchArticle getLastQuery() {
        return mQuery;
    }

    public LiveData<ArrayList<Article>> getSearchList() {
        return searchList;
    }

    public void fetchSearch(Query query) throws TwitterException {
        if (!query.getQuery().equals(mQuery.getQuery())) {
            mQuery.setQuery(query.getQuery());
            mQuery.setSinceId(query.getSinceId());
            mQuery.setMaxId(query.getMaxId());
        }
        QueryResult list = search(query);
        long max = Math.min(list.getMaxId(), mQuery.getMaxId() > 0 ? mQuery.getMaxId() : list.getMaxId());
        long since = Math.max(list.getSinceId(), mQuery.getSinceId());
        mQuery.setMaxId(max);
        mQuery.setSinceId(since);
        searchList.postValue(ConvertUtils.toArticleList(list, getCurrentUserId()));
    }

    public QueryResult search(Query query) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.search(query);
    }

    public LiveData<ArrayList<UserInfo>> getSearchUserList() {
        return searchUserList;
    }

    public void fetchSearchUser(QuerySearchUser query) throws TwitterException {
        if (!query.getQuery().equals(mUserQuery.getQuery())) {
            mUserQuery.setQuery(query.getQuery());
            mUserQuery.setMaxPage(0);
        }
        ResponseList<User> list = searchUser(query.getQuery(), query.getPage());
        int maxPage = Math.max(query.getPage(), mUserQuery.getMaxPage());
        mUserQuery.setMaxPage(maxPage);
        searchUserList.postValue(ConvertUtils.toUserInfoList(list, getCurrentUserId()));
    }

    public ResponseList<User> searchUser(String query, int page) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.searchUsers(query, page);
    }
    // End of search

    public ResponseList<Place> search(GeoQuery geoQuery) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.searchPlaces(geoQuery);
    }

    public void signOut() {
        mTwitter = TwitterFactory.getSingleton();
    }

    public UserInfo getLoggedInUser() throws TwitterException {
        if (loggedInUser == null) {
            User user = mTwitter.verifyCredentials();
            currentUserId = user.getId();
            loggedInUser = ConvertUtils.toUserInfo(user, currentUserId);
        }
        return loggedInUser;
    }

    public long getCurrentUserId() throws RuntimeException {
        if (currentUserId <= INVALID_ID) throw new RuntimeException("twitter id is invalid");
        return currentUserId;
    }

    public MutableLiveData<UserInfo> getUserProfile() {
        return userProfile;
    }

    public void fetchUser(long userId) throws TwitterException {
        ResponseList<User> users = mTwitter.lookupUsers(userId);
        ArrayList<UserInfo> userArrayList = ConvertUtils.toUserInfoList(users, getCurrentUserId());
        userProfile.postValue(userArrayList.get(0));
    }

    public ArrayList<UserInfo> getSearchUser(QuerySearchUser query) throws TwitterException {

        switch (query.getQueryType()) {
            case QuerySearchUser.FIRST:
                mUserQuery = query;
                // 1 페이지가 첫번째 페이지
                mUserQuery.setPage(1);
                break;
            case QuerySearchUser.NEXT:
                mUserQuery.setPage(mUserQuery.getMaxPage());
                break;
        }

        ResponseList<User> result = null;
        result = mTwitter.searchUsers(mUserQuery.getQuery(), mUserQuery.getPage());
        if (result != null) {
            mUserQuery.setMaxPage(mUserQuery.getPage() + 1);
            System.out.println("current : " + mUserQuery.getPage() + " , NEXT : " + mUserQuery.getMaxPage() + " , size: " + result.size());
        }
        return ConvertUtils.toUserInfoList(result, getCurrentUserId());
    }

    public ArrayList<Article> getSearch(QuerySearchArticle query) throws TwitterException {

        Query tQuery = new Query(mQuery.getQuery()).count(Constants.PAGE_CNT);

        switch (query.getQueryType()) {
            case QuerySearchArticle.FIRST:
                mQuery = query;
                tQuery.setQuery(mQuery.getQuery());
                break;
            case QuerySearchArticle.PAST:
                tQuery.setMaxId(mQuery.getMaxId());
                break;
            case QuerySearchArticle.RECENT:
                tQuery.setSinceId(mQuery.getSinceId());
                break;
        }

        QueryResult result = null;
        result = mTwitter.search(tQuery);
        if (result != null) {
            long[] ids = ConvertUtils.getSmallAndLargeId(result);

            System.out.println("PREVIOUS : " + ids[0] + " , NEXT : " + ids[1] + " , size: " + (result.getTweets() != null ? result.getTweets().size() : 0));
            switch (query.getQueryType()) {
                case QuerySearchArticle.FIRST:
                    mQuery.setSinceId(ids[1]);
                    mQuery.setMaxId(ids[0]);
                    break;
                case QuerySearchArticle.PAST:
                    mQuery.setMaxId(Math.min(mQuery.getMaxId(), ids[0]));
                    break;
                case QuerySearchArticle.RECENT:
                    System.out.println("next result\n" + result.toString());
                    mQuery.setSinceId(Math.max(mQuery.getSinceId(), ids[1]));
                    break;
            }
        }
        return ConvertUtils.toArticleList(result, getCurrentUserId());
    }

    public ArrayList<Article> getSearchNearBy(QueryArticleNearBy query) throws TwitterException {

        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        System.out.println(dateString);
        Query tQuery = new Query("until:" + dateString).count(Constants.PAGE_CNT);

        switch (query.getQueryType()) {
            case QueryArticleNearBy.FIRST:
                mQueryNearBy = query;
                break;
            case QueryArticleNearBy.PAST:
                tQuery.setMaxId(mQueryNearBy.getMaxId());
                break;
            case QueryArticleNearBy.RECENT:
                tQuery.setSinceId(mQueryNearBy.getSinceId());
                break;
        }
        GeoLocation geoLocation = new GeoLocation(mQueryNearBy.getLatitude(), mQueryNearBy.getLongitudu());
        tQuery.setGeoCode(geoLocation, mQueryNearBy.getRadius(), Query.Unit.km);

        QueryResult result = null;
        result = mTwitter.search(tQuery);
        if (result != null) {
            long[] ids = ConvertUtils.getSmallAndLargeId(result);

            System.out.println("PREVIOUS : " + ids[0] + " , NEXT : " + ids[1] + " , size: " + (result.getTweets() != null ? result.getTweets().size() : 0));
            switch (query.getQueryType()) {
                case QueryArticleNearBy.FIRST:
                    mQueryNearBy.setSinceId(ids[1]);
                    mQueryNearBy.setMaxId(ids[0]);
                    break;
                case QueryArticleNearBy.PAST:
                    mQueryNearBy.setMaxId(Math.min(mQueryNearBy.getMaxId(), ids[0]));
                    break;
                case QueryArticleNearBy.RECENT:
                    mQueryNearBy.setSinceId(Math.max(mQueryNearBy.getSinceId(), ids[1]));
                    break;
            }
        }
        return ConvertUtils.toArticleList(result, getCurrentUserId());
    }

    public ArrayList<UserInfo> getFollowList(QueryFollow follow) throws TwitterException {

        switch (follow.getQueryType()) {
            case QueryFollow.FIRST:
                queryFollow = follow;
                break;
            case QueryFollow.PREVIOUS:
                follow.setCursor(queryFollow.getPreviosCursor());
                break;
            case QueryFollow.NEXT:
                follow.setCursor(queryFollow.getNextCursor());
                break;
        }

        PagableResponseList<User> list = null;
        if (follow.isFollower()) {
            list = mTwitter.getFollowersList(follow.getUserId(), follow.getCursor(), Constants.USER_LIST_COUNT);
        } else {
            list = mTwitter.getFriendsList(follow.getUserId(), follow.getCursor(), Constants.USER_LIST_COUNT);
        }
        if (list != null) {
            System.out.println("next : " + list.getNextCursor() + " , previous : " + list.getPreviousCursor() + " , size: " + list.size());
            switch (follow.getQueryType()) {
                case QueryFollow.FIRST:
                    queryFollow.setNextCursor(list.getNextCursor());
                    queryFollow.setPreviosCursor(list.getPreviousCursor());
                    break;
                case QueryFollow.PREVIOUS:
                    queryFollow.setPreviosCursor(list.getPreviousCursor());
                    break;
                case QueryFollow.NEXT:
                    queryFollow.setNextCursor(list.getNextCursor());
                    break;
            }
        }
        return ConvertUtils.toUserInfoList(list, getCurrentUserId());
    }
}
