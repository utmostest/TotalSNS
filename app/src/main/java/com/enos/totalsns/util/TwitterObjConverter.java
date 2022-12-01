package com.enos.totalsns.util;

import android.util.Log;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.FollowInfo;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.collection.ArraySet;
import androidx.collection.LongSparseArray;
import twitter4j.DirectMessage;
import twitter4j.DirectMessageList;
import twitter4j.Friendship;
import twitter4j.GeoLocation;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

public class TwitterObjConverter {

    public static String getUserMessagePK(long tableUser, long articleId) {
        return tableUser + "_" + articleId;
    }

    //tableUserId_articleId primary key for Article class
    public static String getUserArticlePK(long tableUser, long articleId, boolean isMention) {
        return tableUser + "_" + articleId + "_" + isMention;
    }

    public static String[] toStringArray(MediaEntity[] urls) {
        if (urls == null) return null;

        String[] strs = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            strs[i] = urls[i].getMediaURL();
        }
        return strs;
    }

    public static HashMap<String, String> toStringHashMap(URLEntity[] urlEntities) {
        if (urlEntities == null) return null;

        HashMap<String, String> urlMap = new HashMap<>();
        for (URLEntity entity : urlEntities) {
            urlMap.put(entity.getText(), entity.getExpandedURL());
        }
        return urlMap;
    }

    public static long[] getUserIdSet(DirectMessageList list, long myId) {
        if (list != null && list.size() > 0) {
            ArraySet<Long> longArraySet = new ArraySet<>();
            for (DirectMessage dm : list) {
                longArraySet.add(myId == dm.getSenderId() ? dm.getRecipientId() : dm.getSenderId());
            }
            Long[] array = longArraySet.toArray(new Long[0]);
            return longObjArrToPriLongArr(array);
        }
        return new long[0];
    }

    private static long[] longObjArrToPriLongArr(Long[] object) {
        if (object == null) return null;
        long[] result = new long[object.length];
        for (int i = 0; i < object.length; i++) {
            result[i] = object[i];
        }
        return result;
    }

    public static LongSparseArray<UserInfo> getUserIdMap(ResponseList<User> userList, long longUserId) {
        if (userList == null) return null;

        LongSparseArray<UserInfo> userHashMap = new LongSparseArray<>();
        for (User user : userList) {
            //add temporary follow info only available isMe
            UserInfo userInfo = toUserInfo(user, new FollowInfo(false, false, longUserId == user.getId()), longUserId);
            userHashMap.put(user.getId(), userInfo);
        }
        return userHashMap;
    }

    public static long[] getSmallAndLargeId(QueryResult result) {
        if (result == null || result.getTweets().size() == 0) {
            return new long[]{0, 0};
        }
        long[] id = new long[]{Long.MAX_VALUE, 0};
        for (Status status : result.getTweets()) {
            id[0] = Math.min(id[0], status.getId());
            id[1] = Math.max(id[1], status.getId());
        }
        if (id[0] > 2) id[0] -= 1;
        return id;
    }

    public static long[] getSmallAndLargeId(ResponseList<Status> result) {
        if (result == null || result.size() == 0) {
            return new long[]{0, 0};
        }
        long[] id = new long[]{Long.MAX_VALUE, 0};
        for (Status status : result) {
            id[0] = Math.min(id[0], status.getId());
            id[1] = Math.max(id[1], status.getId());
        }
        if (id[0] > 2) id[0] -= 1;
        return id;
    }

    public static ArrayList<Article> toArticleList(QueryResult list, long currentUser) {
        if (list == null) return null;
        List<Status> statuses = list.getTweets();
        ArrayList<Article> searches = new ArrayList<>();

        for (Status status : statuses) {
            Article search = toArticle(status, currentUser);
            searches.add(search);
        }
        return searches;
    }

    public static ArrayList<Article> toArticleList(ResponseList<Status> statuses, long currentUser, boolean isMention) {
        if (statuses == null) return null;
        ArrayList<Article> articles = new ArrayList<>();

        for (Status status : statuses) {
            Article search = toArticle(status, currentUser, isMention);
            articles.add(search);
        }
        return articles;
    }

    public static Article toArticle(Status status, long currentUserId) {
        return toArticle(status, currentUserId, false);
    }

    public static Article toMention(Status status, long currentUserId) {
        return toArticle(status, currentUserId, true);
    }

    public static Article toArticle(Status status, long currentUserId, boolean isMentionDb) {
        User user = status.getUser();
        long articleId = status.getId();
        Article article = new Article(
                TwitterObjConverter.getUserArticlePK(currentUserId, articleId, isMentionDb), currentUserId, articleId,
                user.getScreenName(), user.getName(), status.getText(), user.get400x400ProfileImageURL(),
                toStringArray(status.getMediaEntities()), status.getCreatedAt().getTime(), Constants.TWITTER,
                toStringHashMap(status.getURLEntities()), user.getId());
        GeoLocation geoLocation = status.getGeoLocation();
        Place place = status.getPlace();
        setArticleLocationIfExist(article, geoLocation, place);
        article.setMention(isMentionDb);
        return article;
    }

    private static void setArticleLocationIfExist(Article article, GeoLocation geoLocation, Place place) {
        if (geoLocation != null) {
            article.setLatitude(geoLocation.getLatitude());
            article.setLongitude(geoLocation.getLongitude());
        } else if (place != null) {
            if (place.getGeometryCoordinates() != null) {
                double latitude = 0;
                double longitude = 0;
                int count = 0;
                for (GeoLocation[] location : place.getGeometryCoordinates()) {
                    for (GeoLocation loc : location) {
                        latitude += loc.getLatitude();
                        longitude += loc.getLongitude();
                        count++;
                    }
                }
                article.setLatitude(latitude / count);
                article.setLongitude(longitude / count);
            } else if (place.getBoundingBoxCoordinates() != null) {
                double latitude = 0;
                double longitude = 0;
                int count = 0;
                for (GeoLocation[] location : place.getBoundingBoxCoordinates()) {
                    for (GeoLocation loc : location) {
                        latitude += loc.getLatitude();
                        longitude += loc.getLongitude();
                        count++;
                    }
                }
                article.setLatitude(latitude / count);
                article.setLongitude(longitude / count);
            }
        }
    }

    public static ArrayList<Message> toMessageList(DirectMessageList list, long currentUserId, LongSparseArray<UserInfo> userMap) {

        ArrayList<Message> dmList = new ArrayList<Message>();
        for (DirectMessage dm : list) {
            long senderId = currentUserId == dm.getSenderId() ? dm.getRecipientId() : dm.getSenderId();
            UserInfo sender = userMap.get(senderId);
            assert sender != null;
            Message message = toMessage(dm, currentUserId, sender.getUserName(), sender.getUserId(), sender.getProfileImg());
            dmList.add(message);
        }
        return dmList;
    }

    public static Message toMessage(DirectMessage dm, long currentUserId, String senderName, String senderScreenId, String senderProfile) {

        Message message = new Message(TwitterObjConverter.getUserMessagePK(currentUserId, dm.getId()), currentUserId, dm.getId(),
                dm.getRecipientId(), dm.getSenderId(), senderName, senderScreenId, senderProfile,
                dm.getText(), dm.getCreatedAt().getTime(), Constants.TWITTER, currentUserId == dm.getSenderId() ? dm.getRecipientId() : dm.getSenderId());
        return message;
    }

    public static ArrayList<UserInfo> toUserInfoList(ResponseList<User> list, ResponseList<Friendship> friendships, long currentUserId) {
        if (list == null) return null;
        LongSparseArray<FollowInfo> followInfos = getFollowMap(friendships, currentUserId);

        ArrayList<UserInfo> userList = new ArrayList<UserInfo>();
        for (User user : list) {
            FollowInfo followInfo = followInfos == null ? null : followInfos.get(user.getId());
            UserInfo userInfo = toUserInfo(user, followInfo, currentUserId);
            userList.add(userInfo);
        }
        return userList;
    }

    public static UserInfo toUserInfo(User user, FollowInfo followInfo, long currentUserId) {
        Status status = user.getStatus();

        Article article = null;
        if (status != null) {
            article = new Article(
                    TwitterObjConverter.getUserArticlePK(currentUserId, status.getId(), false), currentUserId, status.getId(),
                    user.getScreenName(), user.getName(), status.getText(), user.get400x400ProfileImageURL(),
                    toStringArray(status.getMediaEntities()), status.getCreatedAt().getTime(), Constants.TWITTER,
                    toStringHashMap(status.getURLEntities()), user.getId());
            article.setMention(false);
        }
        UserInfo userInfo = new UserInfo(user.getId(), user.getScreenName(), user.getName(), user.getDescription(),
                user.get400x400ProfileImageURL(), user.getProfileBanner1500x500URL(), user.getProfileBackgroundColor(),
                user.isProtected(), Constants.TWITTER, user.getLocation(), user.getCreatedAt().getTime(),
                user.getEmail(), article, user.getFollowersCount(), user.getFriendsCount(), user.isFollowRequestSent());
        userInfo.setFollowInfo(followInfo);
        return userInfo;
    }

    public static long[] getUserIdList(ResponseList<User> users) {
        if (users == null) return null;
        long[] userIds = new long[users.size()];
        for (int i = 0; i < users.size(); i++) {
            userIds[i] = users.get(i).getId();
        }
        return userIds;
    }

    public static LongSparseArray<FollowInfo> getFollowMap(ResponseList<Friendship> friendships, long myId) {
        if (friendships == null) return null;
        LongSparseArray<FollowInfo> followInfos = new LongSparseArray<>();
        for (int i = 0; i < friendships.size(); i++) {
            Friendship fs = friendships.get(i);
            Log.i(fs.getName(), fs.getId() + "," + myId);
            followInfos.put(fs.getId(), new FollowInfo(fs.isFollowedBy(), fs.isFollowing(), myId == fs.getId()));
        }
        return followInfos;
    }
}
