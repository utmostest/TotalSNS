package com.enos.totalsns.data;

import static com.enos.totalsns.data.Constants.INVALID_ID;
import static com.enos.totalsns.data.Constants.INVALID_POSITION;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.enos.totalsns.util.CompareUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

@Entity(tableName = "article")
public class Article implements Parcelable, Cloneable {

    @PrimaryKey
    @NonNull
    private String tablePlusArticleId;
    private long tableUserId;
    private long articleId;
    private String userId;
    private String userName;
    private String message;
    private String profileImg;
    @Nullable
    private String[] imageUrls;
    private long postedAt;
    private int snsType;
    @Nullable
    private HashMap<String, String> urlMap;
    private boolean isMention;
    private long longUserId;
    private long sinceId = INVALID_ID;
    private double latitude = INVALID_POSITION;
    private double longitude = INVALID_POSITION;


    //룸은 하나의 생성자만 인식해야 하므로 나머지 생성자엔 ignore 어노테이션 사용
    public Article() {

    }

    @Ignore
    public Article(String tablePlusArticleId, long tableId, long articleId, String id, String name, String msg, String profile,
                   @Nullable String[] imgUrls, long time, int snsType, @Nullable HashMap<String, String> urlMap, long longUserId) {
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
        this.isMention = false;
        this.longUserId = longUserId;
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
        isMention = in.readByte() != 0;
        longUserId = in.readLong();
        sinceId = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
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
        dest.writeByte((byte) (isMention ? 1 : 0));
        dest.writeLong(longUserId);
        dest.writeLong(sinceId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
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

    public boolean isMention() {
        return isMention;
    }

    public void setMention(boolean mention) {
        isMention = mention;
    }

    public long getLongUserId() {
        return longUserId;
    }

    public void setLongUserId(long longUserId) {
        this.longUserId = longUserId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getSinceId() {
        return sinceId;
    }

    public void setSinceId(long sinceId) {
        this.sinceId = sinceId;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(tablePlusArticleId, tableUserId, articleId, userId, userName, message, profileImg, postedAt, snsType, urlMap, isMention, longUserId, sinceId, latitude, longitude);
        result = 31 * result + Arrays.hashCode(imageUrls);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article)) return false;
        Article article = (Article) o;
        return tableUserId == article.tableUserId && articleId == article.articleId && postedAt == article.postedAt && snsType == article.snsType && isMention == article.isMention && longUserId == article.longUserId && sinceId == article.sinceId && Double.compare(article.latitude, latitude) == 0 && Double.compare(article.longitude, longitude) == 0 && tablePlusArticleId.equals(article.tablePlusArticleId) && userId.equals(article.userId) && Objects.equals(userName, article.userName) && Objects.equals(message, article.message) && Objects.equals(profileImg, article.profileImg) && Arrays.equals(imageUrls, article.imageUrls) && Objects.equals(urlMap, article.urlMap);
    }

    @Override
    public String toString() {
        return "Article{" +
                "tablePlusArticleId='" + tablePlusArticleId + '\'' +
                ", tableUserId=" + tableUserId +
                ", articleId=" + articleId +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                ", profileImg='" + profileImg + '\'' +
                ", imageUrls=" + Arrays.toString(imageUrls) +
                ", postedAt=" + postedAt +
                ", snsType=" + snsType +
                ", urlMap=" + urlMap +
                ", isMention=" + isMention +
                ", longUserId=" + longUserId +
                ", sinceId=" + sinceId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @NonNull
    @Override
    public Article clone() throws CloneNotSupportedException {
        return (Article) super.clone();
    }
}
