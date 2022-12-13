package com.enos.totalsns.data.source.remote;

public class QueryTimeline {

    private int queryType;
    private long sinceId;
    private long maxId;

    public QueryTimeline(int queryType) {
        this.queryType = queryType;
    }

    public static final int FIRST = 1;
    public static final int PAST = 2;
    public static final int RECENT = 3;
    public static final int BETWEEN = 4;

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public long getSinceId() {
        return sinceId;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setSinceId(long sinceId) {
        this.sinceId = sinceId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }
}
