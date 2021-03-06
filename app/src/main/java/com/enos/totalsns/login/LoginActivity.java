package com.enos.totalsns.login;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.webkit.WebSettings;

import com.enos.totalsns.ContentsActivity;
import com.enos.totalsns.R;
import com.enos.totalsns.data.source.remote.OauthToken;
import com.enos.totalsns.databinding.ActivityLoginBinding;
import com.enos.totalsns.intro.LoginResult;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.ViewModelFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // TODO SNS 별 프래그먼트 생성 및 분기
    // TODO 페이스북과 인스타그램의 페이크 로그인 구현

    public static final String SNS_TYPE_KEY = "SNS_TYPE_KEY";

    private LoginViewModel viewModel;

    private ActivityLoginBinding mDataBinding;

    private AtomicBoolean mHasActivityStarted = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setTitle(R.string.title_activity_login);

        Intent data = getIntent();
        if (data != null) {
            int snsType = data.getIntExtra(SNS_TYPE_KEY, -1);
        }

        setupUI();
        startLogin();
    }

    private void setupUI() {

        viewModel = ViewModelProviders.of(LoginActivity.this, ViewModelFactory.getInstance(this)).get(LoginViewModel.class);

        TwitterWebViewClient twitterWebViewClient = new TwitterWebViewClient();
        twitterWebViewClient.setTwitterLoginListener(result -> {
            if (result.getLoginStatus() == LoginResult.STATUS_LOGIN_SUCCEED) {
                webviewLoginSucceed(result.getToken(), result.getTokenSecret());
            } else {
                loginCanceled("user canceled login");
            }
        });
        mDataBinding.loginWebview.setWebViewClient(twitterWebViewClient);

        WebSettings webSettings = mDataBinding.loginWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private void startLogin() {
        viewModel.signInFirstStep();
        viewModel.getLoginResult().observe(this, result -> {
            if (result == null) return;

            switch (result.getLoginStep()) {
                case LoginResult.STEP1_INIT:
                    if (result.getLoginStatus() == LoginResult.STATUS_LOGIN_SUCCEED) {
                        mDataBinding.loginWebview.loadUrl(result.getAuthorizationUrl());
                    } else {
                        loginFailed(result.getMessage());
                    }
                    break;
                case LoginResult.STEP2_AUTHORIZATION:
                    break;
                case LoginResult.STEP3_ENTIRELOGIN:
                    if (result.getLoginStatus() == LoginResult.STATUS_LOGIN_SUCCEED) {
                        entireLoginSucceed();
                    } else {
                        loginFailed(result.getMessage());
                    }
                    break;
            }
        });
    }

    private void loginCanceled(String message) {
        SingletonToast.getInstance().log(message);
        viewModel.signInFirstStep();
    }

    private void loginFailed(String message) {
        SingletonToast.getInstance().log(message);
        viewModel.signOut();
        viewModel.signInFirstStep();
    }

    private void webviewLoginSucceed(String token, String oauthSecret) {
        OauthToken oauthToken = new OauthToken(token, oauthSecret);

        viewModel.signInTwitterWithOauthToken(oauthToken, true);
    }

    private void entireLoginSucceed() {
        finishAndstartTimelineActivity();
    }

    private void finishAndstartTimelineActivity() {
        if (mHasActivityStarted.compareAndSet(false, true)) {
            ContentsActivity.start(this);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void start(AppCompatActivity context, int snsType) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.SNS_TYPE_KEY, snsType);

//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }
}