package com.enos.totalsns.accounts;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.enos.totalsns.ContentsActivity;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.ActivityAccountsBinding;
import com.enos.totalsns.intro.LoginResult;
import com.enos.totalsns.login.LoginActivity;
import com.enos.totalsns.util.ViewModelFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class AccountsActivity extends AppCompatActivity implements OnSnsAccountListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private AccountsViewModel viewModel;
    private int snsType = Constants.TWITTER;

    ActivityAccountsBinding mDataBinding;
    LiveData<LoginResult> loginResultLiveData;

    private AtomicBoolean mHasActivityStarted = new AtomicBoolean(false);

    // TODO 페이스북 과 인스타그램의 더미계정 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_accounts);

        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(this)).get(AccountsViewModel.class);

        mDataBinding.navigation.setOnNavigationItemSelectedListener(this);
        mDataBinding.acAccFab.setOnClickListener((v) -> onNewAccountButtonClicked(snsType));
        initFragment();
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.accounts_frag_container, AccountFragment.newInstance(Constants.DEFAULT_SNS)).commit();
    }

    private void loginTwitter(Account account) {
        loginResultLiveData = viewModel.getLoginResult(account, true);
        loginResultLiveData.observe(this, result -> {
            if (result == null) return;
            if (result.getLoginStatus() == LoginResult.STATUS_LOGIN_SUCCEED) {
                onLoginSucceed(result.getAccount());
            } else {
                onLoginFailed(result.getMessage());
            }
        });
    }

    private void finishAndStartActivity(Class<?> activity) {
        if (mHasActivityStarted.compareAndSet(false, true)) {
            if (activity.isAssignableFrom(LoginActivity.class)) {
                LoginActivity.start(this, Constants.TWITTER);
            } else if (activity.isAssignableFrom(ContentsActivity.class)) {
                ContentsActivity.start(this);
            }
            finish();
        }
    }

    private void onLoginFailed(String message) {
        finishAndStartActivity(LoginActivity.class);
    }

    private void onLoginSucceed(Account account) {
        finishAndStartActivity(ContentsActivity.class);
    }

    @Override
    public void onAccountClicked(Account item) {
        if (item.getSnsType() == Constants.TWITTER) {
            loginTwitter(item);
        }
    }

    @Override
    public void onNewAccountButtonClicked(int snsType) {
        finishAndStartActivity(LoginActivity.class);
    }

    private String getSNSTypeString(int snsType) {
        String snsStr = null;
        if (snsType == Constants.TWITTER) {
            snsStr = getString(R.string.title_twitter);
        } else if (snsType == Constants.FACEBOOK) {
            snsStr = getString(R.string.title_facebook);
        } else if (snsType == Constants.INSTAGRAM) {
            snsStr = getString(R.string.title_instagram);
        }
        return snsStr;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        boolean menuSelected = false;

        switch (item.getItemId()) {
            case R.id.navigation_twitter:
                snsType = Constants.TWITTER;
                menuSelected = true;
                break;
            case R.id.navigation_facebook:
                snsType = Constants.FACEBOOK;
                menuSelected = true;
                break;
            case R.id.navigation_instagram:
                snsType = Constants.INSTAGRAM;
                menuSelected = true;
                break;
        }

        fragmentManager.beginTransaction().replace(R.id.accounts_frag_container, AccountFragment.newInstance(snsType)).commit();

        return menuSelected;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AccountsActivity.class);
        context.startActivity(intent);
    }
}
