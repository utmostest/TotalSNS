package com.enos.totalsns.accounts;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.ActivityAccountsBinding;
import com.enos.totalsns.intro.LoginResult;
import com.enos.totalsns.login.LoginActivity;
import com.enos.totalsns.ContentsActivity;
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

    Observer<LoginResult> observer = result -> {
        if (result.getLoginStatus() == LoginResult.STATUS_LOGIN_SUCCEED) {
            onLoginSucceed(result.getAccount());
        } else {
            onLoginFailed(result.getMessage());
        }
    };

    private void loginTwitter(Account account) {
        loginResultLiveData = viewModel.getLoginResult(account, true);
        loginResultLiveData.observe(this, observer);
    }

    private void finishAndStartActivity(Class<?> activity) {
        if (mHasActivityStarted.compareAndSet(false, true)) {
            finish();
            Intent intent = new Intent(this, activity);
            startActivity(intent);
        }
    }

    private void finishAndStartActivity(Intent intent) {
        if (mHasActivityStarted.compareAndSet(false, true)) {
            finish();
            startActivity(intent);
        }
    }

    private void onLoginFailed(String message) {
        Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
        intent.putExtra(LoginActivity.SNS_TYPE_KEY, Constants.TWITTER);
        finishAndStartActivity(intent);
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
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.SNS_TYPE_KEY, snsType);
        finishAndStartActivity(intent);
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
}
