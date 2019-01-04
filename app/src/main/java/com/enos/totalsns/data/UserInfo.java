package com.enos.totalsns.data;

// 타인의 개인정보라 룸에 저장하지 않음
public class UserInfo {

    private long longUserId;
    private String userId;
    private String userName;
    private String message;
    private String profileImg;
    private String profileBackImg;
    private String profileBackColor;
    private int snsType;
    private boolean isFollowed;
    private String location;
    private long createdAt;
    private String email;
    private Article lastArticle;
    private int followerCount;
    private int followingCount;

    public UserInfo() {
    }

    public UserInfo(long longUserId, String userId, String userName, String message, String profileImg,
                    String profileBackImg, String profileBackColor, boolean isFollowed, int snsType,
                    String location, long createdAt, String email, Article lastArticle, int followerCount, int followingCount) {
        this.longUserId = longUserId;
        this.userId = userId;
        this.userName = userName;
        this.message = message;
        this.profileImg = profileImg;
        this.profileBackImg = profileBackImg;
        this.profileBackColor = profileBackColor;
        this.isFollowed = isFollowed;
        this.snsType = snsType;
        this.location = location;
        this.createdAt = createdAt;
        this.email = email;
        this.lastArticle = lastArticle;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public int getSnsType() {
        return snsType;
    }

    public long getLongUserId() {
        return longUserId;
    }

    public void setLongUserId(long longUserId) {
        this.longUserId = longUserId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getProfileBackImg() {
        return profileBackImg;
    }

    public void setProfileBackImg(String profileBackImg) {
        this.profileBackImg = profileBackImg;
    }

    public String getProfileBackColor() {
        return profileBackColor;
    }

    public void setProfileBackColor(String profileBackColor) {
        this.profileBackColor = profileBackColor;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public void setSnsType(int snsType) {
        this.snsType = snsType;
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Article getLastArticle() {
        return lastArticle;
    }

    public void setLastArticle(Article lastArticle) {
        this.lastArticle = lastArticle;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
}
