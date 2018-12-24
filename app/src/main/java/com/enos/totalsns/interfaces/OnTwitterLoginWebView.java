package com.enos.totalsns.interfaces;

public interface OnTwitterLoginWebView {
    void onWebViewLoginCanceled();

    void onWebViewLoginSucceed(String callbackUrl, String oauthToken, String oauthSecret);
}
