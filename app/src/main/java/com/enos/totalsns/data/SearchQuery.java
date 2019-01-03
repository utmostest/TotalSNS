package com.enos.totalsns.data;

public class SearchQuery {
    private String query;
    private long sinceId;
    private long maxId;

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
