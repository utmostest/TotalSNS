package com.enos.totalsns.data.source.remote;

import com.enos.totalsns.data.account.Account;

public class OauthToken {
    private String token;
    private String secret;
    private boolean isAccessToken = false;

    public OauthToken(String token, String secret) {
        this.token = token;
        this.secret = secret;
    }

    public OauthToken(Account account) {
        this.token = account.getOauthKey();
        this.secret = account.getOauthSecret();
        this.isAccessToken = true;
    }

    public boolean isAccessToken() {
        return isAccessToken;
    }

    public void setIsAccessToken(boolean accessToken) {
        isAccessToken = accessToken;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
