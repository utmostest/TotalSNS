package com.enos.totalsns.follow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enos.totalsns.R;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.profile.ProfileActivity;
import com.enos.totalsns.profile.ProfileFragment;
import com.enos.totalsns.search.OnUserClickListener;

public class FollowListActivity extends AppCompatActivity implements OnUserClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);
        if (savedInstanceState == null) {
            QueryFollow queryFollow = getIntent().getParcelableExtra(FollowListFragment.ARG_QUERY_FOLLOW);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FollowListFragment.newInstance(queryFollow))
                    .commitNow();
        }
    }

    @Override
    public void onUserItemClicked(UserInfo item) {
        startProfileActivity(item.getLongUserId());
    }

    @Override
    public void onFollowButtonClicked(UserInfo info) {

    }

    private void startProfileActivity(long userId) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileFragment.ARG_USER_ID, userId);
        startActivity(intent);
    }
}
