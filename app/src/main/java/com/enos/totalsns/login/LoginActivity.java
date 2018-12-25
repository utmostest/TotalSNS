package com.enos.totalsns.login;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.data.account.Account;
import com.enos.totalsns.data.source.remote.OauthToken;
import com.enos.totalsns.timelines.TimelineActivity;
import com.enos.totalsns.SnsClientViewModel;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // TODO SNS 별 프래그먼트 생성 및 분기
    // TODO 페이스북과 인스타그램의 페이크 로그인 구현

    public static final String SNS_TYPE_KEY = "SNS_TYPE_KEY";

    private WebView webView;
    private SnsClientViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.title_activity_login);

        Intent data = getIntent();
        if (data != null) {
            int snsType = data.getIntExtra(SNS_TYPE_KEY, -1);
            Toast.makeText(this, "SNS Type : " + snsType, Toast.LENGTH_SHORT).show();
        }

        setupUI();
    }

    OnTwitterLoginListener onTwitterLoginListener = new OnTwitterLoginListener() {
        @Override
        public void onLoginFailed(String message) {
            loginFailed(message);
        }

        @Override
        public void onLoginSucceed(Account account) {
            entireLoginSucceed(account.getScreenName());
        }
    };

    OnTwitterInitListener onTwitterAuthorization = new OnTwitterInitListener() {
        @Override
        public void onTwitterInit(String authorization) {
            webView.loadUrl(authorization);
        }
    };

    private void setupUI() {

        webView = findViewById(R.id.login_webview);
        viewModel = ViewModelProviders.of(LoginActivity.this).get(SnsClientViewModel.class);

        TwitterWebViewClient twitterWebViewClient = new TwitterWebViewClient();
        twitterWebViewClient.setTwitterLoginListener(new OnTwitterLoginWebViewListener() {
            @Override
            public void onWebViewLoginCanceled() {
                loginFailed("user canceled login");
            }

            @Override
            public void onWebViewLoginSucceed(String url, String oauthToken, String oauthSecret) {
                webviewLoginSucceed(url, oauthToken, oauthSecret);
            }
        });
        webView.setWebViewClient(twitterWebViewClient);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        startLogin();
    }

    private void startLogin() {
        viewModel.init(onTwitterAuthorization);
    }

    private void loginFailed(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        viewModel.signOut();
        viewModel.requestAuthorizationUrl(onTwitterAuthorization);
    }

    private void webviewLoginSucceed(String url, String token, String oauthSecret) {
        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();

        OauthToken oauthToken = new OauthToken(token, oauthSecret);

        viewModel.signInTwitterWithOauthToken(oauthToken, onTwitterLoginListener, true);
    }

    private void entireLoginSucceed(String message) {
        finish();
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, TimelineActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}