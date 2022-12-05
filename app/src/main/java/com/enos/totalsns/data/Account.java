package com.enos.totalsns.data;


import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.enos.totalsns.util.CompareUtils;

@Entity(tableName = "account")
public class Account {

    @PrimaryKey
    private long id;
    private String screenName;
    private String oauthKey;
    private String oauthSecret;
    private String profileImage;
    private String name;
    private int snsType;
    private boolean isCurrent;

    public Account() {
    }

    public Account(long uid, String screen_name, String oauth_key, String oauth_secret, String profileImage, String name, int snsType, boolean isCurrent) {
        this.id = uid;
        this.screenName = screen_name;
        this.oauthKey = oauth_key;
        this.oauthSecret = oauth_secret;
        this.profileImage = profileImage;
        this.name = name;

        this.snsType = snsType;
        this.isCurrent = isCurrent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screen_name) {
        this.screenName = screen_name;
    }

    public String getOauthKey() {
        return oauthKey;
    }

    public void setOauthKey(String oauth_key) {
        this.oauthKey = oauth_key;
    }

    public String getOauthSecret() {
        return oauthSecret;
    }

    public void setOauthSecret(String oauth_secret) {
        this.oauthSecret = oauth_secret;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getSnsType() {
        return snsType;
    }

    public void setSnsType(int snsType) {
        this.snsType = snsType;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(this.id).hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Account) {
            return CompareUtils.isAccountSame(this, (Account) obj);
        }
        return false;
    }
}
