package com.enos.totalsns.data.source.remote;

public class QueryMessage {
    private int queryType;
    private String cursor;

    public QueryMessage(int queryType) {
        this.queryType = queryType;
    }

    public QueryMessage(int queryType, String cursor) {
        this.cursor = cursor;
        this.queryType = queryType;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public static final int FIRST = 1;
    public static final int NEXT = 2;
}
