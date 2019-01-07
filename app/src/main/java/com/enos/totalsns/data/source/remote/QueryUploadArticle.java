package com.enos.totalsns.data.source.remote;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;

public class QueryUploadArticle {
    private String message;
    private LatLng geoLocation;
    private File[] uploadingFiles;

    public QueryUploadArticle(String message) {
        this.message = message;
    }

    public QueryUploadArticle(String message, LatLng geoLocation, File[] uploadingFiles) {
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

    public File[] getUploadingFiles() {
        return uploadingFiles;
    }

    public void setUploadingFiles(File[] uploadingFiles) {
        this.uploadingFiles = uploadingFiles;
    }
}
