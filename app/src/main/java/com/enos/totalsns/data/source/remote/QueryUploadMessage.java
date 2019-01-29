package com.enos.totalsns.data.source.remote;

import com.enos.totalsns.data.UserInfo;

public class QueryUploadMessage {
    private UserInfo receiver;
    private String message;
    private String uploadingFile;

    public QueryUploadMessage(UserInfo receiver, String message) {
        this.receiver = receiver;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserInfo getReceiver() {
        return receiver;
    }

    public void setReceiver(UserInfo receiver) {
        this.receiver = receiver;
    }

    public String getUploadingFile() {
        return uploadingFile;
    }

    public void setUploadingFile(String uploadingFile) {
        this.uploadingFile = uploadingFile;
    }
}
