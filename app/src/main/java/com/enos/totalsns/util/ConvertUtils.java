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

    public static Article toArticle(Status status, long currentUserId, boolean isMentionDb) {
        User user = status.getUser();
        long articleId = status.getId();
//        Log.i("status", status + "");
//        String simplifiedText = removeMediaUrl(status);
        Article article = new Article(
                ConvertUtils.getUserNObjectPK(currentUserId, articleId), currentUserId, articleId,
                user.getScreenName(), user.getName(), status.getText(), user.get400x400ProfileImageURL(),
                toStringArray(status.getMediaEntities()), status.getCreatedAt().getTime(), Constants.TWITTER,
                toStringHashMap(status.getURLEntities()), user.getId());
        article.setMention(isMentionDb);
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
        return isStringEqual(oldArticle.getTablePlusArticleId(), newArticle.getTablePlusArticleId()) &&
                isStringEqual(oldArticle.getProfileImg(), newArticle.getProfileImg()) &&
                isStringEqual(oldArticle.getUserName(), newArticle.getUserName()) &&
                isStringEqual(oldArticle.getUserId(), newArticle.getUserId()) &&
                isStringEqual(oldArticle.getMessage(), newArticle.getMessage()) &&
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
                isStringEqual(oldArticle.getProfileImg(), newArticle.getProfileImg()) &&
                isStringEqual(oldArticle.getUserName(), newArticle.getUserName()) &&
                isStringEqual(oldArticle.getUserId(), newArticle.getUserId()) &&
                isStringEqual(oldArticle.getMessage(), newArticle.getMessage()) &&
                isStringEqual(oldArticle.getProfileBackColor(), newArticle.getProfileBackColor()) &&
                isStringEqual(oldArticle.getProfileBackImg(), newArticle.getProfileBackImg()) &&
                oldArticle.getSnsType() == newArticle.getSnsType();
    }

    public static boolean isStringEqual(String one, String other) {
        if (one == null && other == null) return true;
        else if (one == null || other == null) return false;
        else return one.equals(other);
    }

    public static boolean isSearchSame(Search oldSearch, Search newSearch) {
        if (oldSearch == null && newSearch == null) return true;
        else if (oldSearch == null || newSearch == null) return false;

        if (oldSearch instanceof Article && newSearch instanceof Article) {
            return isArticleSame((Article) oldSearch, (Article) newSearch);
        } else if (oldSearch instanceof UserInfo && newSearch instanceof UserInfo) {
            return isUserInfoSame((UserInfo) oldSearch, (UserInfo) newSearch);
        } else {
            return false;
        }
    }

    public static ArrayList<Article> toArticleList(QueryResult list, long currentUser) {
        if (list == null) return null;
        List<Status> statuses = list.getTweets();
        ArrayList<Article> searches = new ArrayList<>();

        for (Status status : statuses) {
            SingletonToast.getInstance().log(list.getQuery(), status.toString());
            Article search = toArticle(status, currentUser);
            searches.add(search);
        }
        return searches;
    }

    public static ArrayList<UserInfo> toUserInfoList(ResponseList<User> list, long currentUserId) {
        if (list == null) return null;
        ArrayList<UserInfo> userList = new ArrayList<UserInfo>();
        for (User user : list) {

            Status status = user.getStatus();

            Article article = null;
            if (status != null) {
                article = new Article(
                        ConvertUtils.getUserNObjectPK(currentUserId, status.getId()), currentUserId, status.getId(),
                        user.getScreenName(), user.getName(), status.getText(), user.get400x400ProfileImageURL(),
                        toStringArray(status.getMediaEntities()), status.getCreatedAt().getTime(), Constants.TWITTER,
                        toStringHashMap(status.getURLEntities()), user.getId());
                article.setMention(false);
            }
            UserInfo userInfo = new UserInfo(user.getId(), user.getScreenName(), user.getName(), user.getDescription(),
                    user.get400x400ProfileImageURL(), user.getProfileBackgroundImageURL(), user.getProfileBackgroundColor(),
                    user.isFollowRequestSent(), Constants.TWITTER, user.getLocation(), user.getCreatedAt().getTime(),
                    user.getEmail(), article, user.getFollowersCount(), user.getFriendsCount());

            userList.add(userInfo);
        }
        return userList;
    }

    public static int compareSearch(Search one, Search other) {
        if (one instanceof Article && other instanceof UserInfo) {
            return 1;
        } else if (one instanceof UserInfo && other instanceof Article) {
            return -1;
        } else if (one instanceof UserInfo && other instanceof UserInfo) {
            long different = (((UserInfo) other).getLongUserId() - ((UserInfo) one).getLongUserId());
            if (different > 0) {
                return 1;
            } else if (different < 0) {
                return -1;
            }
            return 0;
        } else if (one instanceof Article && other instanceof Article) {
            long different = (((Article) other).getArticleId() - ((Article) one).getArticleId());
            if (different > 0) {
                return 1;
            } else if (different < 0) {
                return -1;
            }
            return 0;
        }
        return 0;
    }
}
