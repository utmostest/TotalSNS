package com.enos.totalsns.userlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.data.source.remote.QuerySearchUser;
import com.enos.totalsns.databinding.ItemSearchUserBinding;
import com.enos.totalsns.databinding.ItemUserBinding;
import com.enos.totalsns.profile.ProfileActivity;
import com.enos.totalsns.util.ActivityUtils;

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
    }

    @Override
    public void onUserItemClicked(ItemUserBinding binding, UserInfo item) {
        ProfileActivity.startWithTransition(this, binding, item);
    }

    @Override
    public void onSearchUserItemClicked(ItemSearchUserBinding binding, UserInfo item) {

    }

    @Override
    public void onFollowButtonClicked(UserInfo info) {

    }

    public static void startFollowList(AppCompatActivity context, long userId, boolean isFollower) {
        QueryFollow queryFollow = new QueryFollow(QueryFollow.FIRST, userId, -1, isFollower);
        Intent intent = new Intent(context, UserListActivity.class);
        intent.putExtra(UserListFragment.ARG_QUERY_FOLLOW, queryFollow);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }

    public static void startUserList(AppCompatActivity context, String query) {
        QuerySearchUser querySearchUser = new QuerySearchUser(QueryFollow.FIRST, query);
        Intent intent = new Intent(context, UserListActivity.class);
        intent.putExtra(UserListFragment.ARG_QUERY_SEARCH_USER, querySearchUser);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }
}
