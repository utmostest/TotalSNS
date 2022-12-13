package com.enos.totalsns.data.source.remote;

import static com.enos.totalsns.data.Constants.INVALID_ID;

public class QuerySearchArticle {
    private String query;
    private long sinceId = INVALID_ID;
    private long maxId = INVALID_ID;
    private int queryType;

    public static final int FIRST = 1;
    public static final int PAST = 2;
    public static final int RECENT = 3;
    public static final int BETWEEN = 4;

    public QuerySearchArticle(int queryType) {
        this.queryType = queryType;
    }

    public QuerySearchArticle(int queryType, String query) {
        this.queryType = queryType;
        this.query = query;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
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

}
