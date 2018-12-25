package com.enos.totalsns.data.article;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
    private String profileImg;
    private String userName;
    private String userId;
    private String message;
    private String[] imageUrls;
    private long postedAt;

    public Article() {

    }

    public Article(String id, String name, String msg, String profile, long time) {
        userId = id;
        userName = name;
        message = msg;
        profileImg = profile;
        postedAt = time;
    }

    protected Article(Parcel in) {
        profileImg = in.readString();
        userName = in.readString();
        userId = in.readString();
        message = in.readString();
        imageUrls = in.createStringArray();
        postedAt = in.readLong();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(profileImg);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(message);
        dest.writeStringArray(imageUrls);
        dest.writeLong(postedAt);
    }
}
