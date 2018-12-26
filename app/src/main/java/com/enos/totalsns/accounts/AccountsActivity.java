package com.enos.totalsns.accounts;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.ActivityAccountsBinding;
import com.enos.totalsns.intro.LoginResult;
import com.enos.totalsns.login.LoginActivity;
import com.enos.totalsns.timelines.TimelineActivity;
import com.enos.totalsns.util.ViewModelFactory;

public class AccountsActivity extends AppCompatActivity implements OnSnsAccountListener {

    private AccountsViewModel viewModel;
    private int snsType = Constants.TWITTER;

    ActivityAccountsBinding mDataBinding;
    LiveData<LoginResult> loginResultLiveData;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
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
    };

    // TODO 페이스북 과 인스타그램의 더미계정 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_accounts);

        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(this)).get(AccountsViewModel.class);

        mDataBinding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
        finish();
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void finishAndStartActivity(Intent intent) {
//        loginResultLiveData.removeObserver(observer);
        finish();
        startActivity(intent);
    }

    private void onLoginFailed(String message) {
//        loginResultLiveData.removeObserver(observer);
        Toast.makeText(AccountsActivity.this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
        intent.putExtra(LoginActivity.SNS_TYPE_KEY, Constants.TWITTER);
        finishAndStartActivity(intent);
    }

    private void onLoginSucceed(Account account) {
        finishAndStartActivity(TimelineActivity.class);
    }

    @Override
    public void onAccountClicked(Account item) {
        Toast.makeText(this, item.getScreenName() + "\n" + item.getId() + "\n" + getSNSTypeString(item.getSnsType()), Toast.LENGTH_SHORT).show();
        if (item.getSnsType() == Constants.TWITTER) {
            loginTwitter(item);
        }
    }

    @Override
    public void onNewAccountButtonClicked(int snsType) {
        Toast.makeText(this, getSNSTypeString(snsType), Toast.LENGTH_SHORT).show();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.SNS_TYPE_KEY, snsType);
        startActivity(intent);
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
}
