package com.enos.totalsns.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.HashMap;

@Entity(tableName = "article")
public class Article implements Parcelable {

    @PrimaryKey
    @NonNull
    private String tablePlusArticleId;
    private long tableUserId;
    private long articleId;
    private String userId;
    private String userName;
    private String message;
    private String profileImg;
    private String[] imageUrls;
    private long postedAt;
    private int snsType;
    private HashMap<String, String> urlMap;

    //룸은 하나의 생성자만 인식해야 하므로 나머지 생성자엔 ignore 어노테이션 사용
    public Article() {

    }

    @Ignore
    public Article(String tablePlusArticleId, long tableId, long articleId, String id, String name, String msg, String profile, String[] imgUrls, long time, int snsType, HashMap<String, String> urlMap) {
        this.tablePlusArticleId = tablePlusArticleId;
        this.tableUserId = tableId;
        this.articleId = articleId;
        this.userId = id;
        this.userName = name;
        this.message = msg;
        this.profileImg = profile;
        this.imageUrls = imgUrls;
        this.postedAt = time;
        this.snsType = snsType;
        this.urlMap = urlMap;
    }

    @Ignore
    protected Article(Parcel in) {
        tablePlusArticleId = in.readString();
        tableUserId = in.readLong();
        articleId = in.readLong();
        profileImg = in.readString();
        userName = in.readString();
        userId = in.readString();
        message = in.readString();
        imageUrls = in.createStringArray();
        postedAt = in.readLong();
        snsType = in.readInt();
        urlMap = in.readHashMap(String.class.getClassLoader());
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tablePlusArticleId);
        dest.writeLong(tableUserId);
        dest.writeLong(articleId);
        dest.writeString(profileImg);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(message);
        dest.writeStringArray(imageUrls);
        dest.writeLong(postedAt);
        dest.writeInt(snsType);
        dest.writeMap(urlMap);
    }

    public void setTablePlusArticleId(String tablePlusArticleId) {
        this.tablePlusArticleId = tablePlusArticleId;
    }

    public String getTablePlusArticleId() {
        return tablePlusArticleId;
    }

    public long getTableUserId() {
        return tableUserId;
    }

    public void setTableUserId(long tableUserId) {
        this.tableUserId = tableUserId;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(long postedAt) {
        this.postedAt = postedAt;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public int getSnsType() {
        return snsType;
    }

    public void setSnsType(int snsType) {
        this.snsType = snsType;
    }

    public HashMap<String, String> getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(HashMap<String, String> urlMap) {
        this.urlMap = urlMap;
    }
}
