package com.enos.totalsns.data;

// 룸에 저장하지 않음
public class UserInfo implements Search {

    private long longUserId;
    private String userId;
    private String userName;
    private String message;
    private String profileImg;
    private String profileBackImg;
    private String profileBackColor;
    private int snsType;
    private boolean isFollowed;

    public UserInfo() {
    }

    public UserInfo(long longUserId, String userId, String userName, String message, String profileImg,
                    String profileBackImg, String profileBackColor, boolean isFollowed, int snsType) {
        this.longUserId = longUserId;
        this.userId = userId;
        this.userName = userName;
        this.message = message;
        this.profileImg = profileImg;
        this.profileBackImg = profileBackImg;
        this.profileBackColor = profileBackColor;
        this.isFollowed = isFollowed;
        this.snsType = snsType;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getProfileImg() {
        return profileImg;
    }

    @Override
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
}
