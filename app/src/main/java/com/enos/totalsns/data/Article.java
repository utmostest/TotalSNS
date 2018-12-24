package com.enos.totalsns.data;

public class Article {
    private String profileImg;
    private String userName;
    private String userId;
    private String message;
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
}
