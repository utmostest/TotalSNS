package com.enos.totalsns.data.source.remote;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;

public class QueryUploadArticle {
    private String message;
    private LatLng geoLocation;
    private String[] uploadingFiles;

    public QueryUploadArticle(String message) {
        this.message = message;
    }

    public QueryUploadArticle(String message, LatLng geoLocation, String[] uploadingFiles) {
        this.message = message;
        this.geoLocation = geoLocation;
        this.uploadingFiles = uploadingFiles;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LatLng getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(LatLng geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String[] getUploadingFiles() {
        return uploadingFiles;
    }

    public void setUploadingFiles(String[] uploadingFiles) {
        this.uploadingFiles = uploadingFiles;
    }
}
