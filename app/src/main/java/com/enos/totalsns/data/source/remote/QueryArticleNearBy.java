package com.enos.totalsns.data.source.remote;

import com.enos.totalsns.data.Constants;

public class QueryArticleNearBy {
    private double latitude;
    private double longitudu;
    private long sinceId;
    private long maxId;
    private int queryType;

    private double radius = Constants.DEFAULT_RADIUS_KM;

    public static final int FIRST = 1;
    public static final int PAST = 2;
    public static final int RECENT = 3;

    public QueryArticleNearBy(int queryType) {
        this.queryType = queryType;
    }

    public QueryArticleNearBy(int queryType, double latitude, double longitudu) {
        this.queryType = queryType;
        this.latitude = latitude;
        this.longitudu = longitudu;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitudu() {
        return longitudu;
    }

    public void setLongitudu(double longitudu) {
        this.longitudu = longitudu;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
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
