package com.enos.totalsns.accounts;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.SnsClientViewModel;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.login.LoginActivity;
import com.enos.totalsns.login.OnTwitterLoginListener;
import com.enos.totalsns.timelines.TimelineActivity;

public class AccountsActivity extends AppCompatActivity implements OnSnsAccountListener {

    private SnsClientViewModel viewModel;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        FragmentManager fragmentManager = getSupportFragmentManager();

        int snsType = Constants.TWITTER;
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
        setContentView(R.layout.activity_accounts);

        viewModel = ViewModelProviders.of(this).get(SnsClientViewModel.class);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initFragment();
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.accounts_frag_container, AccountFragment.newInstance(Constants.DEFAULT_SNS)).commit();
    }

    private void loginTwitter(Account account) {
        viewModel.signInTwitterWithAccount(account, onTwitterLoginListener, true);
    }

    private void finishAndStartActivity(Class<?> activity) {
        finish();
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void finishAndStartActivity(Intent intent) {
        finish();
        startActivity(intent);
    }

    private OnTwitterLoginListener onTwitterLoginListener = new OnTwitterLoginListener() {
        @Override
        public void onLoginFailed(String message) {
            Toast.makeText(AccountsActivity.this, message, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
            intent.putExtra(LoginActivity.SNS_TYPE_KEY, Constants.TWITTER);
            finishAndStartActivity(intent);
        }

        @Override
        public void onLoginSucceed(Account account) {
            finishAndStartActivity(TimelineActivity.class);
        }
    };

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
