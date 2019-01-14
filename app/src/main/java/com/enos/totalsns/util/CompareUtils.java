package com.enos.totalsns.util;

import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.UserInfo;

import java.util.Arrays;
import java.util.HashMap;

public class CompareUtils {

    public static int compareArticle(Article one, Article other) {
        if (one == null || other == null) return 0;
        long different = (((Article) other).getArticleId() - ((Article) one).getArticleId());
        if (different > 0) {
            return 1;
        } else if (different < 0) {
            return -1;
        }
        return 0;
    }

    public static int compareUserInfo(UserInfo one, UserInfo other) {
        if (one == null || other == null) return 0;
        long different = (((UserInfo) other).getLongUserId() - ((UserInfo) one).getLongUserId());
        if (different > 0) {
            return 1;
        } else if (different < 0) {
            return -1;
        }
        return 0;
    }

    public static boolean isArticleSame(Article oldArticle, Article newArticle) {
        if (newArticle == null && oldArticle == null) return true;
        else if (newArticle == null || oldArticle == null) return false;
        return isStringEqual(oldArticle.getTablePlusArticleId(), newArticle.getTablePlusArticleId());
    }

    public static boolean isArticleEqual(Article oldArticle, Article newArticle) {
        if (newArticle == null && oldArticle == null) return true;
        else if (newArticle == null || oldArticle == null) return false;
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
                equalsHashMap(oldArticle.getUrlMap(), newArticle.getUrlMap());
    }

    public static boolean isAccountSame(Account oldAccount, Account newAccount) {
        return oldAccount.getId() == newAccount.getId();
    }

    public static boolean isAccountEqual(Account oldAccount, Account newAccount) {
        return oldAccount.getId() == newAccount.getId() &&
                oldAccount.getSnsType() == newAccount.getSnsType() &&
                oldAccount.isCurrent() == newAccount.isCurrent() &&
                isStringEqual(oldAccount.getName(), newAccount.getName()) &&
                isStringEqual(oldAccount.getOauthSecret(), newAccount.getOauthSecret()) &&
                isStringEqual(oldAccount.getOauthKey(), newAccount.getOauthKey()) &&
                isStringEqual(oldAccount.getProfileImage(), newAccount.getProfileImage()) &&
                isStringEqual(oldAccount.getScreenName(), newAccount.getScreenName());
    }

    public static boolean isMessageSame(Message oldArticle, Message newArticle) {
        if (newArticle == null && oldArticle == null) return true;
        else if (newArticle == null || oldArticle == null) return false;
        return isStringEqual(oldArticle.getUserDmId(), newArticle.getUserDmId());
    }

    public static boolean isMessageEqual(Message oldMessage, Message newMessage) {
        if (newMessage == null && oldMessage == null) return true;
        else if (newMessage == null || oldMessage == null) return false;
        return oldMessage.getCreatedAt() == newMessage.getCreatedAt() &&
                oldMessage.getSnsType() == newMessage.getSnsType() &&
                oldMessage.getMessageId() == newMessage.getMessageId() &&
                oldMessage.getReceiverId() == newMessage.getReceiverId() &&
                oldMessage.getSenderId() == newMessage.getSenderId() &&
                oldMessage.getTableUserId() == newMessage.getTableUserId() &&
                oldMessage.getSenderTableId() == newMessage.getSenderTableId() &&
                isStringEqual(oldMessage.getMessage(), newMessage.getMessage()) &&
                isStringEqual(oldMessage.getSenderName(), newMessage.getSenderName()) &&
                isStringEqual(oldMessage.getSenderScreenId(), newMessage.getSenderScreenId()) &&
                isStringEqual(oldMessage.getSenderProfile(), newMessage.getSenderProfile()) &&
                isStringEqual(oldMessage.getUserDmId(), newMessage.getUserDmId());
    }

    public static boolean isUserInfoSame(UserInfo oldArticle, UserInfo newArticle) {
        if (newArticle == null && oldArticle == null) return true;
        else if (newArticle == null || oldArticle == null) return false;
        return oldArticle.getLongUserId() == newArticle.getLongUserId();
    }

    public static boolean isUserInfoEqual(UserInfo oldItem, UserInfo newItem) {
        if (newItem == null && oldItem == null) return true;
        else if (newItem == null || oldItem == null) return false;
        return oldItem.getCreatedAt() == newItem.getCreatedAt() &&
                oldItem.getSnsType() == newItem.getSnsType() &&
                oldItem.getLongUserId() == newItem.getLongUserId() &&
                oldItem.getFollowerCount() == newItem.getFollowerCount() &&
                oldItem.getFollowingCount() == newItem.getFollowingCount() &&
                isStringEqual(oldItem.getUserName(), newItem.getUserName()) &&
                isStringEqual(oldItem.getUserId(), newItem.getUserId()) &&
                isStringEqual(oldItem.getProfileBackImg(), newItem.getProfileBackImg()) &&
                isStringEqual(oldItem.getProfileImg(), newItem.getProfileImg()) &&
                isStringEqual(oldItem.getProfileBackColor(), newItem.getProfileBackColor()) &&
                isStringEqual(oldItem.getEmail(), newItem.getEmail()) &&
                isStringEqual(oldItem.getLocation(), newItem.getLocation()) &&
                isStringEqual(oldItem.getMessage(), newItem.getMessage());
//                && isArticleSame(oldItem.getLastArticle(), newItem.getLastArticle());
    }

    public static boolean isStringEqual(String one, String other) {
        if (one == null && other == null) return true;
        else if (one == null || other == null) return false;
        else return one.equals(other);
    }

    public static boolean equalsHashMap(HashMap<String, String> urlMap, HashMap<String, String> urlMap2) {
        boolean isFirstNull = urlMap == null;
        boolean isSecondNull = urlMap2 == null;
        if (isFirstNull && isSecondNull) return true;
        else if (isFirstNull || isSecondNull) return false;
        else return urlMap.equals(urlMap2);
    }
}
