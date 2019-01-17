package com.enos.totalsns.data;

import android.os.Parcel;
import android.os.Parcelable;

public class FollowInfo implements Parcelable {
    private boolean isFollower;
    private boolean isFollowing;
    private boolean isMe;

    public FollowInfo(boolean isFollower, boolean isFollowing, boolean isMe) {
        this.isFollower = isFollower;
        this.isFollowing = isFollowing;
        this.isMe = isMe;
    }

    protected FollowInfo(Parcel in) {
        isFollower = in.readByte() != 0;
        isFollowing = in.readByte() != 0;
        isMe = in.readByte() != 0;
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public static final Creator<FollowInfo> CREATOR = new Creator<FollowInfo>() {
        @Override
        public FollowInfo createFromParcel(Parcel in) {
            return new FollowInfo(in);
        }

        @Override
        public FollowInfo[] newArray(int size) {
            return new FollowInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isFollower ? 1 : 0));
        dest.writeByte((byte) (isFollowing ? 1 : 0));
        dest.writeByte((byte) (isMe ? 1 : 0));
    }
}
