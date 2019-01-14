package com.enos.totalsns.data.source.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.enos.totalsns.BuildConfig;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.util.TwitterObjConverter;

import java.io.File;
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
import twitter4j.UploadedMedia;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager {

    private static TwitterManager mTwitterManager = null;

    private Twitter mTwitter = null;

    private MutableLiveData<ArrayList<Article>> homeTimeline;
    private MutableLiveData<ArrayList<Message>> directMessage;
    private MutableLiveData<ArrayList<Article>> mentionList;

    private UserInfo loggedInUser = null;

    private QuerySearchArticle querySearchArticle;
    private QueryArticleNearBy queryArticleNearBy;
    private QuerySearchUser querySearchUser;
    private QueryFollow queryFollow;
    private QueryMessage queryMessage;
    private QueryTimeline queryTimeline;
    private QueryUserTimeline queryUserTimeline;

    private TwitterManager() {
        init();
    }

    public static TwitterManager getInstance() {
        if (mTwitterManager == null) {
            mTwitterManager = new TwitterManager();
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

        initSearchVariable();
    }

    private void initSearchVariable() {
        if (querySearchArticle == null)
            querySearchArticle = new QuerySearchArticle(QuerySearchArticle.FIRST, "");
        if (querySearchUser == null)
            querySearchUser = new QuerySearchUser(QuerySearchUser.FIRST, "");
        if (queryFollow == null) queryFollow = new QueryFollow(QueryFollow.FIRST, 0, -1, false);
        if (queryArticleNearBy == null)
            queryArticleNearBy = new QueryArticleNearBy(QueryArticleNearBy.FIRST, Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE);
        if (queryMessage == null) queryMessage = new QueryMessage(QueryMessage.FIRST, null);
        if (queryTimeline == null) queryTimeline = new QueryTimeline(QueryTimeline.FIRST);
        if (queryUserTimeline == null)
            queryUserTimeline = new QueryUserTimeline(QueryUserTimeline.FIRST, -1);
    }

    public String getAuthorizationUrl() throws TwitterException {
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

        loggedInUser = TwitterObjConverter.toUserInfo(credential, credential.getId());

        return new Account(loggedInUser.getLongUserId(), loggedInUser.getUserId(), token.getToken(), token.getSecret(),
                loggedInUser.getProfileImg(), loggedInUser.getUserName(), Constants.TWITTER, true);
    }

    public Article updateStatus(QueryUploadArticle query) throws TwitterException {
        StatusUpdate status = new StatusUpdate(query.getMessage());
        if (query.getGeoLocation() != null)
            status.setLocation(new GeoLocation(query.getGeoLocation().latitude, query.getGeoLocation().longitude));
        if (query.getUploadingFiles() != null) {
            long[] mediaIds = new long[query.getUploadingFiles().length];
            int position = 0;
            for (File file : query.getUploadingFiles()) {
                UploadedMedia media = mTwitter.uploadMedia(file);
                mediaIds[position] = media.getMediaId();
                position++;
            }
            status.setMediaIds(mediaIds);
        }
        Article article = TwitterObjConverter.toArticle(mTwitter.updateStatus(status), loggedInUser.getLongUserId());
        return article;
    }

    public LiveData<ArrayList<Article>> getHomeTimeline() {
        return homeTimeline;
    }

    public void fetchTimeline(Paging paging) throws TwitterException {

        ResponseList<Status> list = mTwitter.getHomeTimeline(paging);
        long currentUserId = getLoggedInUser().getLongUserId();
        ArrayList<Article> articleList = TwitterObjConverter.toArticleList(list, currentUserId, false);
        homeTimeline.postValue(articleList);
    }

    public LiveData<ArrayList<Article>> getMention() {
        return mentionList;
    }

    public void fetchMention(Paging paging) throws TwitterException {

        ResponseList<Status> list = mTwitter.getMentionsTimeline(paging);
        long currentUserId = getLoggedInUser().getLongUserId();
        ArrayList<Article> articleList = TwitterObjConverter.toArticleList(list, currentUserId, true);
        mentionList.postValue(articleList);
    }

    public LiveData<ArrayList<Message>> getDirectMessage() {
        return directMessage;
    }

    public void fetchDirectMessage(QueryMessage message) throws TwitterException {
        switch (message.getQueryType()) {
            case QueryMessage.FIRST:
                queryMessage = message;
                break;
            case QueryMessage.NEXT:
                message.setCursor(queryMessage.getCursor());
                break;
        }
        String cursor = message.getCursor();

        DirectMessageList list = (cursor == null || cursor.length() <= 0) ? mTwitter.getDirectMessages(Constants.PAGE_CNT) :
                mTwitter.getDirectMessages(Constants.PAGE_CNT, cursor);

        queryMessage.setCursor(list.getNextCursor());

        long[] userSet = TwitterObjConverter.getUserIdSet(list, getLoggedInUser().getLongUserId());

        ResponseList<User> userList = mTwitter.lookupUsers(userSet);

        directMessage.postValue(
                TwitterObjConverter.toMessageList(list, getLoggedInUser().getLongUserId(),
                        TwitterObjConverter.getUserIdMap(userList, getLoggedInUser().getLongUserId())));
    }

    public Message sendDirectMessage(QueryUploadMessage query, Message user) throws TwitterException {
        long mediaId = 0;
        if (query.getUploadingFile() != null && query.getUploadingFile().length() > 0) {
            UploadedMedia media = mTwitter.uploadMedia(query.getUploadingFile());
            mediaId = media.getMediaId();
        }
        DirectMessage dm = mediaId <= 0 ? mTwitter.sendDirectMessage(query.getUserId(), query.getMessage()) :
                mTwitter.sendDirectMessage(query.getUserId(), query.getMessage(), mediaId);
        return TwitterObjConverter.toMessage(dm, getLoggedInUser().getLongUserId(), user);
    }

    public void signOut() {
        mTwitter = TwitterFactory.getSingleton();
    }

    public UserInfo getLoggedInUser() throws TwitterException {
        if (loggedInUser == null) {
            User user = mTwitter.verifyCredentials();
            loggedInUser = TwitterObjConverter.toUserInfo(user, user.getId());
        }
        return loggedInUser;
    }

    public UserInfo getUserInfo(long userId) throws TwitterException {
        ResponseList<User> users = mTwitter.lookupUsers(userId);
        ArrayList<UserInfo> userArrayList = TwitterObjConverter.toUserInfoList(users, getLoggedInUser().getLongUserId());
        return userArrayList.get(0);
    }

    public ResponseList<Place> search(GeoQuery geoQuery) throws TwitterException {
        if (mTwitter == null) return null;

        return mTwitter.searchPlaces(geoQuery);
    }

    public ArrayList<UserInfo> getSearchUser(QuerySearchUser query) throws TwitterException {

        switch (query.getQueryType()) {
            case QuerySearchUser.FIRST:
                querySearchUser = query;
                // 1 페이지가 첫번째 페이지
                querySearchUser.setPage(1);
                break;
            case QuerySearchUser.NEXT:
                querySearchUser.setPage(querySearchUser.getMaxPage() < 1 ? 1 : querySearchUser.getMaxPage());
                break;
        }

        ResponseList<User> result = null;
        result = mTwitter.searchUsers(querySearchUser.getQuery(), querySearchUser.getPage());
        if (result != null) {
            if (querySearchUser.getPage() < 1) querySearchUser.setPage(1);
            querySearchUser.setMaxPage(querySearchUser.getPage() + 1);
        }
        return TwitterObjConverter.toUserInfoList(result, getLoggedInUser().getLongUserId());
    }

    public ArrayList<Article> getSearch(QuerySearchArticle query) throws TwitterException {

        Query tQuery = new Query(querySearchArticle.getQuery()).count(Constants.PAGE_CNT);

        switch (query.getQueryType()) {
            case QuerySearchArticle.FIRST:
                querySearchArticle = query;
                tQuery.setQuery(query.getQuery());
                break;
            case QuerySearchArticle.PAST:
                tQuery.setMaxId(querySearchArticle.getMaxId());
                break;
            case QuerySearchArticle.RECENT:
                tQuery.setSinceId(querySearchArticle.getSinceId());
                break;
        }

        QueryResult result = null;
        result = mTwitter.search(tQuery);
        if (result != null) {
            long[] ids = TwitterObjConverter.getSmallAndLargeId(result);

            switch (query.getQueryType()) {
                case QuerySearchArticle.FIRST:
                    querySearchArticle.setSinceId(ids[1]);
                    querySearchArticle.setMaxId(ids[0]);
                    break;
                case QuerySearchArticle.PAST:
                    querySearchArticle.setMaxId(Math.min(querySearchArticle.getMaxId(), ids[0]));
                    break;
                case QuerySearchArticle.RECENT:
                    querySearchArticle.setSinceId(Math.max(querySearchArticle.getSinceId(), ids[1]));
                    break;
            }
        }
        return TwitterObjConverter.toArticleList(result, getLoggedInUser().getLongUserId());
    }

    public ArrayList<Article> getSearchNearBy(QueryArticleNearBy query) throws TwitterException {

        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        // default query string for until today
        Query tQuery = new Query("until:" + dateString).count(Constants.PAGE_CNT);

        switch (query.getQueryType()) {
            case QueryArticleNearBy.FIRST:
                queryArticleNearBy = query;
                break;
            case QueryArticleNearBy.PAST:
                tQuery.setMaxId(queryArticleNearBy.getMaxId());
                break;
            case QueryArticleNearBy.RECENT:
                tQuery.setSinceId(queryArticleNearBy.getSinceId());
                break;
        }
        GeoLocation geoLocation = new GeoLocation(queryArticleNearBy.getLatitude(), queryArticleNearBy.getLongitudu());
        tQuery.setGeoCode(geoLocation, queryArticleNearBy.getRadius(), Query.Unit.km);

        QueryResult result = null;
        result = mTwitter.search(tQuery);
        if (result != null) {
            long[] ids = TwitterObjConverter.getSmallAndLargeId(result);

            switch (query.getQueryType()) {
                case QueryArticleNearBy.FIRST:
                    queryArticleNearBy.setSinceId(ids[1]);
                    queryArticleNearBy.setMaxId(ids[0]);
                    break;
                case QueryArticleNearBy.PAST:
                    queryArticleNearBy.setMaxId(Math.min(queryArticleNearBy.getMaxId(), ids[0]));
                    break;
                case QueryArticleNearBy.RECENT:
                    queryArticleNearBy.setSinceId(Math.max(queryArticleNearBy.getSinceId(), ids[1]));
                    break;
            }
        }
        return TwitterObjConverter.toArticleList(result, getLoggedInUser().getLongUserId());
    }

    public ArrayList<UserInfo> getFollowList(QueryFollow follow) throws TwitterException {

        switch (follow.getQueryType()) {
            case QueryFollow.FIRST:
                queryFollow = follow;
                break;
            case QueryFollow.PREVIOUS:
                queryFollow.setCursor(queryFollow.getPreviosCursor());
                break;
            case QueryFollow.NEXT:
                queryFollow.setCursor(queryFollow.getNextCursor());
                break;
        }

        PagableResponseList<User> list = null;
        if (queryFollow.isFollower()) {
            list = mTwitter.getFollowersList(queryFollow.getUserId(), queryFollow.getCursor(), Constants.USER_LIST_COUNT);
        } else {
            list = mTwitter.getFriendsList(queryFollow.getUserId(), queryFollow.getCursor(), Constants.USER_LIST_COUNT);
        }
        if (list != null) {
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
        return TwitterObjConverter.toUserInfoList(list, getLoggedInUser().getLongUserId());
    }

    public ArrayList<Article> getUserTimeline(QueryUserTimeline query) throws TwitterException {
        Paging paging = new Paging().count(Constants.PAGE_CNT);
        switch (query.getQueryType()) {
            case QueryUserTimeline.FIRST:
                queryUserTimeline = query;
                break;
            case QueryUserTimeline.RECENT:
                paging.setSinceId(queryUserTimeline.getSinceId());
                break;
            case QueryUserTimeline.PAST:
                paging.setMaxId(queryUserTimeline.getMaxId() - 1);
                break;
        }
        ResponseList<Status> list = mTwitter.getUserTimeline(queryUserTimeline.getUserId(), paging);
        if (list != null) {
            long[] ids = TwitterObjConverter.getSmallAndLargeId(list);

            switch (query.getQueryType()) {
                case QueryUserTimeline.FIRST:
                    queryUserTimeline.setSinceId(ids[1]);
                    queryUserTimeline.setMaxId(ids[0]);
                    break;
                case QueryUserTimeline.RECENT:
                    queryUserTimeline.setSinceId(Math.max(queryUserTimeline.getSinceId(), ids[1]));
                    break;
                case QueryUserTimeline.PAST:
                    queryUserTimeline.setMaxId(Math.min(queryUserTimeline.getMaxId(), ids[0]));
                    break;
            }
        }
        long currentUserId = getLoggedInUser().getLongUserId();
        return TwitterObjConverter.toArticleList(list, currentUserId, false);
    }
}