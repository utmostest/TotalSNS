package com.enos.totalsns.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.follow.FollowListActivity;
import com.enos.totalsns.follow.FollowListFragment;
import com.enos.totalsns.follow.OnFollowListener;
import com.enos.totalsns.data.source.remote.QueryFollow;

public class ProfileActivity extends AppCompatActivity implements OnFollowListener {

    private long userId;

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
        userId = getIntent().getLongExtra(ProfileFragment.ARG_USER_ID, ProfileFragment.INVALID_ID);
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

    private void startFollowActivity(long userId, boolean isFollower) {
        QueryFollow queryFollow = new QueryFollow(QueryFollow.FIRST, userId, -1, isFollower);
        Intent intent = new Intent(this, FollowListActivity.class);
        intent.putExtra(FollowListFragment.ARG_QUERY_FOLLOW, queryFollow);
        startActivity(intent);
    }

    @Override
    public void onFollowClicked(UserInfo info, boolean isFollower) {
        startFollowActivity(userId, isFollower);
    }
}
