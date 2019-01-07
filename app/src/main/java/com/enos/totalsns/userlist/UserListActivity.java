package com.enos.totalsns.userlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.data.source.remote.QuerySearchUser;
import com.enos.totalsns.profile.ProfileActivity;
import com.enos.totalsns.profile.ProfileFragment;
import com.enos.totalsns.util.SingletonToast;

public class UserListActivity extends AppCompatActivity implements OnUserClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);
        if (savedInstanceState == null) {
            QueryFollow queryFollow = getIntent().getParcelableExtra(UserListFragment.ARG_QUERY_FOLLOW);
            QuerySearchUser querySearchUser = getIntent().getParcelableExtra(UserListFragment.ARG_QUERY_SEARCH_USER);
            if (queryFollow != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, UserListFragment.newInstance(queryFollow))
                        .commitNow();
            } else if (querySearchUser != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, UserListFragment.newInstance(querySearchUser))
                        .commitNow();
            }
        }
        SingletonToast.getInstance().log("instance_id", this.hashCode() + "");
    }

    @Override
    public void onUserItemClicked(UserInfo item) {
        startProfileActivity(item);
    }

    @Override
    public void onFollowButtonClicked(UserInfo info) {

    }

    private void startProfileActivity(UserInfo userInfo) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileFragment.ARG_USER_INFO, userInfo);
        startActivity(intent);
    }
}
