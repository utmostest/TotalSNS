package com.enos.totalsns.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.enos.totalsns.util.CompareUtils;

// 타인의 개인정보라 룸에 저장하지 않음
public class UserInfo implements Parcelable {

    private long longUserId;
    private String userId;
    private String userName;
    private String message;
    private String profileImg;
    private String profileBackImg;
    private String profileBackColor;
    private int snsType;
    private boolean isProtected;
    private String location;
    private long createdAt;
    private String email;
    private Article lastArticle;
    private int followerCount;
    private int followingCount;
    private boolean isFollowReqSend;

    private FollowInfo followInfo = null;
    private RelationInfo relationInfo = null;

    public UserInfo() {
    }

    public UserInfo(long longUserId, String userId, String userName, String message, String profileImg,
                    String profileBackImg, String profileBackColor, boolean isProtected, int snsType,
                    String location, long createdAt, String email, Article lastArticle, int followerCount,
                    int followingCount, boolean isFollowReqSend) {

        this.longUserId = longUserId;
        this.userId = userId;
        this.userName = userName;
        this.message = message;
        this.profileImg = profileImg;
        this.profileBackImg = profileBackImg;
        this.profileBackColor = profileBackColor;
        this.isProtected = isProtected;
        this.snsType = snsType;
        this.location = location;
        this.createdAt = createdAt;
        this.email = email;
        this.lastArticle = lastArticle;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.isFollowReqSend = isFollowReqSend;
    }

    protected UserInfo(Parcel in) {
        longUserId = in.readLong();
        userId = in.readString();
        userName = in.readString();
        message = in.readString();
        profileImg = in.readString();
        profileBackImg = in.readString();
        profileBackColor = in.readString();
        snsType = in.readInt();
        isProtected = in.readByte() != 0;
        location = in.readString();
        createdAt = in.readLong();
        email = in.readString();
        lastArticle = in.readParcelable(Article.class.getClassLoader());
        followerCount = in.readInt();
        followingCount = in.readInt();
        isFollowReqSend = in.readByte() != 0;
        boolean isFollowNotNull = in.readByte() != 0;
        if (isFollowNotNull) followInfo = in.readParcelable(FollowInfo.class.getClassLoader());
        boolean isRelationInfoNotNull = in.readByte() != 0;
        if (isRelationInfoNotNull)
            relationInfo = in.readParcelable(RelationInfo.class.getClassLoader());
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

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

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean followed) {
        isProtected = followed;
    }

    public void setSnsType(int snsType) {
        this.snsType = snsType;
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

    public boolean isFollowReqSend() {
        return isFollowReqSend;
    }

    public void setFollowReqSend(boolean followReqSend) {
        isFollowReqSend = followReqSend;
    }

    public FollowInfo getFollowInfo() {
        return followInfo;
    }

    public void setFollowInfo(FollowInfo followInfo) {
        this.followInfo = followInfo;
    }

    public RelationInfo getRelationInfo() {
        return relationInfo;
    }

    public void setRelationInfo(RelationInfo relationInfo) {
        this.relationInfo = relationInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(longUserId);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(message);
        dest.writeString(profileImg);
        dest.writeString(profileBackImg);
        dest.writeString(profileBackColor);
        dest.writeInt(snsType);
        dest.writeByte((byte) (isProtected ? 1 : 0));
        dest.writeString(location);
        dest.writeLong(createdAt);
        dest.writeString(email);
        dest.writeParcelable(lastArticle, flags);
        dest.writeInt(followerCount);
        dest.writeInt(followingCount);
        dest.writeByte((byte) (isFollowReqSend ? 1 : 0));

        boolean isFollowInfoNotNull = followInfo != null;
        dest.writeByte((byte) (isFollowInfoNotNull ? 1 : 0));
        if (isFollowInfoNotNull) dest.writeParcelable(followInfo, flags);

        boolean isRelationInfoNotNull = relationInfo != null;
        dest.writeByte((byte) (isRelationInfoNotNull ? 1 : 0));
        if (isRelationInfoNotNull) dest.writeParcelable(relationInfo, flags);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(longUserId).hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof UserInfo) {
            return CompareUtils.isUserInfoSame(this, (UserInfo) obj);
        }
        return false;
    }
}
