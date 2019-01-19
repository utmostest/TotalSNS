package com.enos.totalsns.login;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.enos.totalsns.data.Constants;
import com.enos.totalsns.intro.LoginResult;
import com.enos.totalsns.listener.OnTwitterLoginListener;

public class TwitterWebViewClient extends WebViewClient {
    private final String CALLBACK_URL = "twittersdk://";
    private final String TOKEN_KEY = "oauth_token";
    private final String VERFIER_KEY = "oauth_verifier";
    private final String CALLBACK_CANCELED = CALLBACK_URL + "?denied";

    private OnTwitterLoginListener mListener = null;

    public void setTwitterLoginListener(OnTwitterLoginListener listener) {
        mListener = listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(CALLBACK_URL)) {
            LoginResult result = new LoginResult();
            result.setLoginStep(LoginResult.STEP2_AUTHORIZATION);
            result.setSnsType(Constants.TWITTER);
            if (isCanceled(url)) {
                if (mListener != null) {
                    result.setLoginStep(LoginResult.STATUS_LOGIN_CANCELED);
                    result.setMessage("user canceled login");
                    mListener.onLoginResult(result);
                }
                return true;
            }

            Uri uri = Uri.parse(url);
            String oauth_token = uri.getQueryParameter(TOKEN_KEY);
            String oauth_verifier = uri.getQueryParameter(VERFIER_KEY);

            if (isCallbackInvalid(oauth_token, oauth_verifier)) {
                result.setLoginStatus(LoginResult.STATUS_LOGIN_FAILED);
                result.setMessage("invalid oauth token and secret");
                if (mListener != null) mListener.onLoginResult(result);
                return true;
            }

            result.setLoginStatus(LoginResult.STATUS_LOGIN_SUCCEED);
            result.setToken(oauth_token);
            result.setTokenSecret(oauth_verifier);

            if (mListener != null) mListener.onLoginResult(result);
            return true;
        }
        view.loadUrl(url);
        return super.shouldOverrideUrlLoading(view, url);
    }

    private boolean isCanceled(String callback) {
        return callback.startsWith(CALLBACK_CANCELED);
    }

    private boolean isCallbackInvalid(String oauth_token, String oauth_verifier) {
        return (oauth_token == null || oauth_verifier == null || oauth_token.length() == 0 || oauth_verifier.length() == 0);
    }
}