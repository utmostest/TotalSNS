package com.enos.totalsns.data.source.remote;

import java.io.File;

public class QueryUploadMessage {
    private long userId;
    private String message;
    private File uploadingFile;

    public QueryUploadMessage(long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public File getUploadingFile() {
        return uploadingFile;
    }

    public void setUploadingFile(File uploadingFile) {
        this.uploadingFile = uploadingFile;
    }
}
