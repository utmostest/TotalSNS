package com.enos.totalsns.util;

import android.text.format.DateFormat;
import android.util.Log;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;

import java.util.Date;
import java.util.HashMap;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

public class ConverUtils {

    //tableUserId_articleId primary key for Article class
    public static String getTableArticlePK(long tableUser, long articleId) {
        return tableUser + "_" + articleId;
    }

    public static String[] toStringArray(MediaEntity[] urls) {
        if (urls == null) return null;

        String[] strs = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            Log.i("url", "media : " + urls[i]);
            strs[i] = urls[i].getMediaURL();
        }
        return strs;
    }

    public static HashMap<String, String> toStringHashMap(URLEntity[] urlEntities) {
        if (urlEntities == null) return null;

        HashMap<String, String> urlMap = new HashMap<>();
        for (URLEntity entity : urlEntities) {
//            Log.i("url", entity.getText() + "\n" + entity.getExpandedURL());
            urlMap.put(entity.getText(), entity.getExpandedURL());
        }
        return urlMap;
    }

    public static Article toArticle(Status status, long currentUserId) {
        User user = status.getUser();
        long articleId = status.getId();
        Log.i("status", status + "");
//        String simplifiedText = removeMediaUrl(status);
        Article article = new Article(
                ConverUtils.getTableArticlePK(currentUserId, articleId), currentUserId, articleId,
                user.getScreenName(), user.getName(), status.getText(), user.get400x400ProfileImageURL(),
                toStringArray(status.getMediaEntities()), status.getCreatedAt().getTime(), Constants.TWITTER,
                toStringHashMap(status.getURLEntities()));
        return article;
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
}
