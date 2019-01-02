package com.enos.totalsns.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.ArraySet;
import android.text.format.DateFormat;

import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.Search;
import com.enos.totalsns.data.UserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.DirectMessageList;
import twitter4j.MediaEntity;
import twitter4j.QueryResult;
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
                dm.getText(), dm.getCreatedAt().getTime(), Constants.TWITTER, currentUserId == dm.getSenderId() ? dm.getRecipientId() : dm.getSenderId());
        return message;
    }


    public static Message toMessage(DirectMessage dm, long currentUserId, Message sender) {

        Message message = new Message(ConvertUtils.getUserNObjectPK(currentUserId, dm.getId()), currentUserId, dm.getId(),
                dm.getRecipientId(), dm.getSenderId(), sender.getSenderName(), sender.getSenderScreenId(), sender.getSenderProfile(),
                dm.getText(), dm.getCreatedAt().getTime(), Constants.TWITTER, currentUserId == dm.getSenderId() ? dm.getRecipientId() : dm.getSenderId());
        return message;
    }

    public static Article toArticle(Status status, long currentUserId) {
        return toArticle(status, currentUserId, false);
    }

    public static Article toArticle(Status status, long currentUserId, boolean isMention) {
        User user = status.getUser();
        long articleId = status.getId();
//        Log.i("status", status + "");
//        String simplifiedText = removeMediaUrl(status);
        Article article = new Article(
                ConvertUtils.getUserNObjectPK(currentUserId, articleId), currentUserId, articleId,
                user.getScreenName(), user.getName(), status.getText(), user.get400x400ProfileImageURL(),
                toStringArray(status.getMediaEntities()), status.getCreatedAt().getTime(), Constants.TWITTER,
                toStringHashMap(status.getURLEntities()));
        article.setMention(isMention);
        return article;
    }

    public static Article toMention(Status status, long currentUserId) {
        return toArticle(status, currentUserId, true);
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

    public static long getSecondsByMilli(long quitDelayMilli) {
        long sec = quitDelayMilli / 1000;
        return sec;
    }

    public static Account toAccount(User user) {
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmapToDrwable(Context context, Bitmap bitmap) {
        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        return d;
    }

    public static boolean isArticleSame(Article oldArticle, Article newArticle) {
        return oldArticle.getTablePlusArticleId().equals(newArticle.getTablePlusArticleId()) &&
                oldArticle.getProfileImg().equals(newArticle.getProfileImg()) &&
                oldArticle.getUserName().equals(newArticle.getUserName()) &&
                oldArticle.getUserId().equals(newArticle.getUserId()) &&
                oldArticle.getMessage().equals(newArticle.getMessage()) &&
                oldArticle.getTableUserId() == newArticle.getTableUserId() &&
                oldArticle.getArticleId() == newArticle.getArticleId() &&
                oldArticle.getSnsType() == newArticle.getSnsType() &&
                oldArticle.getPostedAt() == newArticle.getPostedAt() &&
                Arrays.equals(oldArticle.getImageUrls(), newArticle.getImageUrls()) &&
                ConvertUtils.equalsHashMap(oldArticle.getUrlMap(), newArticle.getUrlMap());
    }

    public static boolean isUserInfoSame(UserInfo oldArticle, UserInfo newArticle) {
        return oldArticle.getLongUserId() == newArticle.getLongUserId() &&
                oldArticle.isFollowed() == newArticle.isFollowed() &&
                oldArticle.getProfileImg().equals(newArticle.getProfileImg()) &&
                oldArticle.getUserName().equals(newArticle.getUserName()) &&
                oldArticle.getUserId().equals(newArticle.getUserId()) &&
                oldArticle.getMessage().equals(newArticle.getMessage()) &&
                oldArticle.getProfileBackColor().equals(newArticle.getProfileBackColor()) &&
                oldArticle.getProfileBackImg().equals(newArticle.getProfileBackImg()) &&
                oldArticle.getSnsType() == newArticle.getSnsType();
    }

    public static boolean isSearchSame(Search oldSearch, Search newSearch) {
        if (oldSearch instanceof Article && newSearch instanceof Article) {
            return isArticleSame((Article) oldSearch, (Article) newSearch);
        } else if (oldSearch instanceof UserInfo && newSearch instanceof UserInfo) {
            return isUserInfoSame((UserInfo) oldSearch, (UserInfo) newSearch);
        } else {
            return false;
        }
    }

    public static ArrayList<Search> toSearchList(QueryResult list, long currentUser) {
        if (list == null) return null;
        List<Status> statuses = list.getTweets();
        ArrayList<Search> searches = new ArrayList<>();

        for (Status status : statuses) {
            Search search = toArticle(status,currentUser);
            searches.add(search);
        }
        return searches;
    }
}
