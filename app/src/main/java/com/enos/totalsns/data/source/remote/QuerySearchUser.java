package com.enos.totalsns.data.source.remote;

import android.os.Parcel;
import android.os.Parcelable;

public class QuerySearchUser implements Parcelable {
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

    protected QuerySearchUser(Parcel in) {
        query = in.readString();
        maxPage = in.readInt();
        page = in.readInt();
        queryType = in.readInt();
    }

    public static final Creator<QuerySearchUser> CREATOR = new Creator<QuerySearchUser>() {
        @Override
        public QuerySearchUser createFromParcel(Parcel in) {
            return new QuerySearchUser(in);
        }

        @Override
        public QuerySearchUser[] newArray(int size) {
            return new QuerySearchUser[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(query);
        dest.writeInt(maxPage);
        dest.writeInt(page);
        dest.writeInt(queryType);
    }
}