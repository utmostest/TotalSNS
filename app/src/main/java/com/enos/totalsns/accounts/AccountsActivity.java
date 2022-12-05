package com.enos.totalsns.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.enos.totalsns.ContentsActivity;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.ActivityAccountsBinding;
import com.enos.totalsns.intro.LoginResult;
import com.enos.totalsns.listener.OnSnsAccountListener;
import com.enos.totalsns.login.LoginActivity;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.atomic.AtomicBoolean;

public class AccountsActivity extends AppCompatActivity implements OnSnsAccountListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private AccountsViewModel viewModel;
    private int snsType = Constants.TWITTER;

    ActivityAccountsBinding mBinding;
    LiveData<LoginResult> loginResultLiveData;

    private AtomicBoolean mHasActivityStarted = new AtomicBoolean(false);

    // TODO 페이스북 과 인스타그램의 더미계정 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_accounts);

        viewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(this)).get(AccountsViewModel.class);

        mBinding.navigation.setOnNavigationItemSelectedListener(this);
        mBinding.acAccFab.setOnClickListener((v) -> finishAndStartActivity(LoginActivity.class));
        setTitle(R.string.title_activity_accounts);
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
                LoginActivity.start(this, snsType);
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

    public static void start(AppCompatActivity context) {
        Intent intent = new Intent(context, AccountsActivity.class);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context,intent);
    }
}
