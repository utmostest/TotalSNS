package com.enos.totalsns.data.source.remote;

public class QuerySearchUser {
    private String query;
    private int maxPage;
    private int page = 1;
    private int queryType;

    public static final int FIRST = 1;
    public static final int NEXT = 2;

    public QuerySearchUser(int queryType) {
        this.queryType = queryType;
    }

    public QuerySearchUser(int queryType, String query) {
        this.queryType = queryType;
        this.query = query;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }
}