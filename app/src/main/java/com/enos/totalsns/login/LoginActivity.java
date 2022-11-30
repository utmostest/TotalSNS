package com.enos.totalsns.login;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.enos.totalsns.ContentsActivity;
import com.enos.totalsns.R;
import com.enos.totalsns.data.source.remote.OauthToken;
import com.enos.totalsns.databinding.ActivityLoginBinding;
import com.enos.totalsns.intro.LoginResult;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.ViewModelFactory;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements ProviderInstaller.ProviderInstallListener {

    // TODO SNS 별 프래그먼트 생성 및 분기
    // TODO 페이스북과 인스타그램의 페이크 로그인 구현

    public static final String SNS_TYPE_KEY = "SNS_TYPE_KEY";

    private LoginViewModel viewModel;

    private ActivityLoginBinding mBinding;

    private final AtomicBoolean mHasActivityStarted = new AtomicBoolean(false);

    private static final int ERROR_DIALOG_REQUEST_CODE = 1;

    private boolean retryProviderInstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setTitle(R.string.title_activity_login);

        Intent data = getIntent();
        if (data != null) {
            int snsType = data.getIntExtra(SNS_TYPE_KEY, -1);
        }

        ProviderInstaller.installIfNeededAsync(this, this);
        setupUI();
        startLogin();
    }

    private void setupUI() {

        viewModel = ViewModelProviders.of(LoginActivity.this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(this)).get(LoginViewModel.class);

        TwitterWebViewClient twitterWebViewClient = new TwitterWebViewClient();
        twitterWebViewClient.setTwitterLoginListener(result -> {
            if (result.getLoginStatus() == LoginResult.STATUS_LOGIN_SUCCEED) {
                webviewLoginSucceed(result.getToken(), result.getTokenSecret());
            } else {
                loginCanceled();
            }
        });
        mBinding.loginWebview.setWebViewClient(twitterWebViewClient);

        WebSettings webSettings = mBinding.loginWebview.getSettings();
//        webSettings.setJavaScriptEnabled(true);
    }

    private void startLogin() {

        viewModel.signInFirstStep();
        viewModel.getLoginResult().observe(this, result -> {
            if (result == null) return;

            switch (result.getLoginStep()) {
                case LoginResult.STEP1_INIT:
                    if (result.getLoginStatus() == LoginResult.STATUS_LOGIN_SUCCEED) {
                        mBinding.loginWebview.loadUrl(result.getAuthorizationUrl());
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

    private void loginCanceled() {
        SingletonToast.getInstance().log("user canceled login");
//        viewModel.signInFirstStep();
    }

    private void loginFailed(String message) {
        SingletonToast.getInstance().log(message);
        viewModel.signOut();
//        viewModel.signInFirstStep();
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

    public static void start(AppCompatActivity context, int snsType) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.SNS_TYPE_KEY, snsType);

//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
            // Adding a fragment via GoogleApiAvailability.showErrorDialogFragment
            // before the instance state is restored throws an error. So instead,
            // set a flag here, which will cause the fragment to delay until
            // onPostResume.
            retryProviderInstall = true;
        }
    }

    /**
     * On resume, check to see if we flagged that we need to reinstall the
     * provider.
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (retryProviderInstall) {
            // We can now safely retry installation.
            ProviderInstaller.installIfNeededAsync(this, this);
        }
        retryProviderInstall = false;
    }

    private void onProviderInstallerNotAvailable() {
        // This is reached if the provider cannot be updated for some reason.
        // App should consider all HTTP communication to be vulnerable, and take
        // appropriate action.
        Toast.makeText(this, R.string.unable_to_use, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderInstallFailed(int errorCode, @Nullable Intent intent) {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        if (availability.isUserResolvableError(errorCode)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            availability.showErrorDialogFragment(
                    this,
                    errorCode,
                    ERROR_DIALOG_REQUEST_CODE,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // The user chose not to take the recovery action
                            onProviderInstallerNotAvailable();
                        }
                    });
        } else {
            // Google Play services is not available.
            onProviderInstallerNotAvailable();
        }
    }

    @Override
    public void onProviderInstalled() {
        if (viewModel != null) viewModel.signInFirstStep();
    }
}