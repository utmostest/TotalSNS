package com.enos.totalsns.data.source.remote;

public class QueryUserTimeline {

    private int queryType;
    private long userId;
    private long sinceId;
    private long maxId;

    public static final int FIRST = 1;
    public static final int PAST = 2;
    public static final int RECENT = 3;

    public QueryUserTimeline(int queryType) {
        this.queryType = queryType;
    }

    public QueryUserTimeline(int queryType, long userId) {
        this.queryType = queryType;
        this.userId = userId;
    }

    public long getSinceId() {
        return sinceId;
    }

    public void setSinceId(long sinceId) {
        this.sinceId = sinceId;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }
}
