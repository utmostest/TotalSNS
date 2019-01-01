package com.enos.totalsns.util;

import android.annotation.SuppressLint;
import android.support.v4.util.ArraySet;
import android.text.format.DateFormat;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Mention;
import com.enos.totalsns.data.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import twitter4j.DirectMessage;
import twitter4j.DirectMessageList;
import twitter4j.MediaEntity;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

public class ConvertUtils {

    //tableUserId_articleId primary key for Article class
    public static String getUserNObjectPK(long tableUser, long articleId) {
        return tableUser + "_" + articleId;
    }

    public static String[] toStringArray(MediaEntity[] urls) {
        if (urls == null) return null;

        String[] strs = new String[urls.length];
//        Log.i("array", strs.length + "," + urls.length);
        for (int i = 0; i < urls.length; i++) {
//            Log.i("url", "media : " + urls[i]);
            strs[i] = urls[i].getMediaURL();
        }
        return strs;
    }

    public static HashMap<String, String> toStringHashMap(URLEntity[] urlEntities) {
        if (urlEntities == null) return null;

        HashMap<String, String> urlMap = new HashMap<>();
        for (URLEntity entity : urlEntities) {
            //Log.i("url", entity.getText() + "\n" + entity.getExpandedURL());
            urlMap.put(entity.getText(), entity.getExpandedURL());
        }
        return urlMap;
    }

    public static Message toMessage(DirectMessage dm, long currentUserId, User sender) {

        Message message = new Message(ConvertUtils.getUserNObjectPK(currentUserId, dm.getId()), currentUserId, dm.getId(),
                dm.getRecipientId(), dm.getSenderId(), sender.getName(), sender.getScreenName(), sender.get400x400ProfileImageURL(),
                dm.getText(), dm.getCreatedAt().getTime(), Constants.TWITTER);
        return message;
    }

    public static Article toArticle(Status status, long currentUserId) {
        User user = status.getUser();
        long articleId = status.getId();
//        Log.i("status", status + "");
//        String simplifiedText = removeMediaUrl(status);
        Article article = new Article(
                ConvertUtils.getUserNObjectPK(currentUserId, articleId), currentUserId, articleId,
                user.getScreenName(), user.getName(), status.getText(), user.get400x400ProfileImageURL(),
                toStringArray(status.getMediaEntities()), status.getCreatedAt().getTime(), Constants.TWITTER,
                toStringHashMap(status.getURLEntities()));
        return article;
    }

    public static Mention toMention(Status status, long currentUserId) {
        User user = status.getUser();
        long articleId = status.getId();
//        Log.i("status", status + "");
//        String simplifiedText = removeMediaUrl(status);
        Mention mention = new Mention(
                ConvertUtils.getUserNObjectPK(currentUserId, articleId), currentUserId, articleId,
                user.getScreenName(), user.getName(), status.getText(), user.get400x400ProfileImageURL(),
                toStringArray(status.getMediaEntities()), status.getCreatedAt().getTime(), Constants.TWITTER,
                toStringHashMap(status.getURLEntities()));
        return mention;
    }

    private static String removeMediaUrl(Status status) {
        String result = status.getText();
        MediaEntity[] mediaEntities = status.getMediaEntities();
        if (mediaEntities == null || mediaEntities.length <= 0) return result;
        result.replace(mediaEntities[0].getURL(), "");
        return result;
    }

    public static String getDateString(long dateTime) {
        Date date = new Date();
        date.setTime(dateTime);
        CharSequence dateStr = DateFormat.format("yyyy.M.d h:m a", date);
        return dateTime == 0 ? "" : dateStr.toString();
    }

    public static int getActualSize(String[] strings) {
        int size = 0;
        if (strings == null) return size;

        for (String str : strings) {
            if (isStringValid(str)) size++;
        }

        return size;
    }

    public static boolean isStringValid(String string) {
        return string != null && string.length() > 0;
    }

    public static boolean equalsHashMap(HashMap<String, String> urlMap, HashMap<String, String> urlMap2) {
        boolean isFirstNull = urlMap == null;
        boolean isSecondNull = urlMap2 == null;
        if (isFirstNull && isSecondNull) return true;
        else if (isFirstNull || isSecondNull) return false;
        else return urlMap.equals(urlMap2);
    }

    public static ArrayList<Message> toMessageList(DirectMessageList list, long currentUserId, HashMap<Long, User> userMap) {

        ArrayList<Message> dmList = new ArrayList<Message>();
        int num = 0;
        for (DirectMessage dm : list) {
            num++;
            SingletonToast.getInstance().log(dm.toString());
            long senderId = currentUserId == dm.getSenderId() ? dm.getRecipientId() : dm.getSenderId();
            Message message = toMessage(dm, currentUserId, userMap.get(senderId));
            dmList.add(message);
        }
        return dmList;
    }

    public static long[] getUserIdSet(DirectMessageList list, long myId) {
        if (list != null && list.size() > 0) {
            ArraySet<Long> longArraySet = new ArraySet<>();
            for (DirectMessage dm : list) {
                longArraySet.add(myId == dm.getSenderId() ? dm.getRecipientId() : dm.getSenderId());
                SingletonToast.getInstance().log(dm + "");
            }
            Long[] array = longArraySet.toArray(new Long[longArraySet.size()]);
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

    public static HashMap<Long, User> getUserIdMap(ResponseList<User> userList) {
        if (userList == null) return null;

        @SuppressLint("UseSparseArrays") HashMap<Long, User> userHashMap = new HashMap<>();
        for (User user : userList) {
            userHashMap.put(user.getId(), user);
        }
        return userHashMap;
    }

    public static Article toArticle(Mention source) {
        if (source == null) return null;
        Article dest = new Article();
        dest.setSnsType(source.getSnsType());
        dest.setImageUrls(source.getImageUrls());
        dest.setMessage(source.getMessage());
        dest.setUserId(source.getUserId());
        dest.setArticleId(source.getArticleId());
        dest.setPostedAt(source.getPostedAt());
        dest.setProfileImg(source.getProfileImg());
        dest.setTablePlusArticleId(source.getTablePlusArticleId());
        dest.setUrlMap(source.getUrlMap());
        dest.setUserName(source.getUserName());
        dest.setTableUserId(source.getTableUserId());
        return dest;
    }

    public static Mention toMention(Article source) {
        if (source == null) return null;
        Mention dest = new Mention();
        dest.setSnsType(source.getSnsType());
        dest.setImageUrls(source.getImageUrls());
        dest.setMessage(source.getMessage());
        dest.setUserId(source.getUserId());
        dest.setArticleId(source.getArticleId());
        dest.setPostedAt(source.getPostedAt());
        dest.setProfileImg(source.getProfileImg());
        dest.setTablePlusArticleId(source.getTablePlusArticleId());
        dest.setUrlMap(source.getUrlMap());
        dest.setUserName(source.getUserName());
        dest.setTableUserId(source.getTableUserId());
        return dest;
    }
}
