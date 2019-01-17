package com.enos.totalsns.data;

import android.os.Parcel;
import android.os.Parcelable;

public class RelationInfo implements Parcelable {
    protected RelationInfo(Parcel in) {
    }

    public static final Creator<RelationInfo> CREATOR = new Creator<RelationInfo>() {
        @Override
        public RelationInfo createFromParcel(Parcel in) {
            return new RelationInfo(in);
        }

        @Override
        public RelationInfo[] newArray(int size) {
            return new RelationInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
