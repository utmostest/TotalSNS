package com.enos.totalsns.data.source.remote;

import android.os.Parcel;
import android.os.Parcelable;

public class QueryFollow implements Parcelable {
    private long userId;
    private boolean isFollower;
    // -1 is first query cursor
    private long cursor = -1;
    private long nextCursor;
    private long previosCursor;
    private int queryType;

    public static final int FIRST = 1;
    public static final int NEXT = 2;
    public static final int PREVIOUS = 3;

    public QueryFollow(int type) {
        queryType = type;
    }

    public QueryFollow(int type, long userId, long cursor, boolean isFollower) {
        this.queryType = type;
        this.userId = userId;
        this.cursor = cursor;
        this.isFollower = isFollower;
    }

    protected QueryFollow(Parcel in) {
        queryType = in.readInt();
        userId = in.readLong();
        isFollower = in.readByte() != 0;
        cursor = in.readLong();
        nextCursor = in.readLong();
        previosCursor = in.readLong();
    }

    public static final Creator<QueryFollow> CREATOR = new Creator<QueryFollow>() {
        @Override
        public QueryFollow createFromParcel(Parcel in) {
            return new QueryFollow(in);
        }

        @Override
        public QueryFollow[] newArray(int size) {
            return new QueryFollow[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(queryType);
        dest.writeLong(userId);
        dest.writeByte((byte) (isFollower ? 1 : 0));
        dest.writeLong(cursor);
        dest.writeLong(nextCursor);
        dest.writeLong(previosCursor);
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }

    public long getCursor() {
        return cursor;
    }

    public void setCursor(long cursor) {
        this.cursor = cursor;
    }

    public long getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(long nextCursor) {
        this.nextCursor = nextCursor;
    }

    public long getPreviosCursor() {
        return previosCursor;
    }

    public void setPreviosCursor(long previosCursor) {
        this.previosCursor = previosCursor;
    }
}
