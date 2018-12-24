package com.enos.totalsns;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.enos.totalsns.interfaces.OnTwitterLoginWebView;

public class TwitterWebViewClient extends WebViewClient {
    public static String CALLBACK_URL = "twittersdk://";

    private OnTwitterLoginWebView mListener = null;

    public void setTwitterLoginListener(OnTwitterLoginWebView listener) {
        mListener = listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(CALLBACK_URL)) {
            if (url.startsWith(CALLBACK_URL + "?denied")) {
                if (mListener != null) mListener.onWebViewLoginCanceled();
                return true;
            }

            Uri uri = Uri.parse(url);
            String oauth_token = uri.getQueryParameter("oauth_token");
            String oauth_verifier = uri.getQueryParameter("oauth_verifier");

            if (mListener != null) mListener.onWebViewLoginSucceed(url, oauth_token, oauth_verifier);
            return true;
        }
        view.loadUrl(url);
        return super.shouldOverrideUrlLoading(view, url);
    }
}