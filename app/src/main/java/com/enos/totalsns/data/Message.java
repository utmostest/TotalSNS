package com.enos.totalsns.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.enos.totalsns.util.CompareUtils;

@Entity(tableName = "message")
public class Message {

    @PrimaryKey
    @NonNull
    private String userDmId;
    private long tableUserId;
    private long messageId;
    private long receiverId;
    private long senderId;
    private String senderName;
    private String senderScreenId;
    private String senderProfile;
    private String message;
    private long createdAt;
    private int snsType;
    private long senderTableId;
    private long maxCreatedAt;

    public Message(@NonNull String userDmId, long tableUserId, long messageId, long receiverId, long senderId,
                   String senderName, String senderScreenId, String senderProfile, String message, long createdAt, int snsType, long senderTableId) {
        this.userDmId = userDmId;
        this.tableUserId = tableUserId;
        this.messageId = messageId;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderScreenId = senderScreenId;
        this.senderProfile = senderProfile;
        this.message = message;
        this.createdAt = createdAt;
        this.snsType = snsType;
        this.senderTableId = senderTableId;
    }

    @NonNull
    public String getUserDmId() {
        return userDmId;
    }

    public void setUserDmId(@NonNull String userDmId) {
        this.userDmId = userDmId;
    }

    public long getTableUserId() {
        return tableUserId;
    }

    public void setTableUserId(long tableUserId) {
        this.tableUserId = tableUserId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderScreenId() {
        return senderScreenId;
    }

    public void setSenderScreenId(String senderScreenId) {
        this.senderScreenId = senderScreenId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderProfile() {
        return senderProfile;
    }

    public void setSenderProfile(String senderProfile) {
        this.senderProfile = senderProfile;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getSnsType() {
        return snsType;
    }

    public void setSnsType(int snsType) {
        this.snsType = snsType;
    }

    public long getSenderTableId() {
        return senderTableId;
    }

    public void setSenderTableId(long senderTableId) {
        this.senderTableId = senderTableId;
    }

    public long getMaxCreatedAt() {
        return maxCreatedAt;
    }

    public void setMaxCreatedAt(long maxCreatedAt) {
        this.maxCreatedAt = maxCreatedAt;
    }

    @Override
    public int hashCode() {
        return userDmId.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Message) {
            return CompareUtils.isMessageEqual(this, (Message) obj);
        }
        return false;
    }
}
