package com.enos.totalsns.profile;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.enos.totalsns.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (savedInstanceState == null) {
            initFragment();
        }
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initFragment() {
        long userId = getIntent().getLongExtra(ProfileFragment.ARG_USER_ID, ProfileFragment.INVALID_ID);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, ProfileFragment.newInstance(userId)).commitNow();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
//            navigateUpTo(new Intent(this, ContentsActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
