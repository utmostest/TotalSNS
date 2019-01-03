package com.enos.totalsns.data;

public class SearchUserQuery {
    private String query;
    private int minPage;
    private int maxPage;
    private int page;

    public SearchUserQuery(String query, int page) {
        this.query = query;
        this.page = page;
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

    public int getMinPage() {
        return minPage;
    }

    public void setMinPage(int minPage) {
        this.minPage = minPage;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }
}