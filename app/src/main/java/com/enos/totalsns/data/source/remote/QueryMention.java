package com.enos.totalsns.data.source.remote;

public class QueryMention {

    private int queryType;

    public QueryMention(int queryType) {
        this.queryType = queryType;
    }

    public static final int FIRST = 1;
    public static final int PAST = 2;
    public static final int RECENT = 3;

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }
}
