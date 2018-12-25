package com.enos.totalsns.login;

public interface OnTwitterLoginWebViewListener {
    void onWebViewLoginCanceled();

    void onWebViewLoginSucceed(String callbackUrl, String oauthToken, String oauthSecret);
}
