package com.enos.totalsns.userlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.data.source.remote.QuerySearchUser;
import com.enos.totalsns.databinding.ActivityFollowListBinding;
import com.enos.totalsns.databinding.ItemUserBinding;
import com.enos.totalsns.listener.OnUserClickListener;
import com.enos.totalsns.profile.ProfileActivity;
import com.enos.totalsns.profile.ProfileViewModel;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ViewModelFactory;

public class UserListActivity extends AppCompatActivity implements OnUserClickListener {

    ActivityFollowListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityFollowListBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_follow_list);
        if (savedInstanceState == null) {
            QueryFollow queryFollow = getIntent().getParcelableExtra(UserListFragment.ARG_QUERY_FOLLOW);
            QuerySearchUser querySearchUser = getIntent().getParcelableExtra(UserListFragment.ARG_QUERY_SEARCH_USER);
            if (queryFollow != null) {
                ProfileViewModel viewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(this)).get(ProfileViewModel.class);
                UserInfo current = viewModel.getuserFromCache(queryFollow.getUserId());
                setTitle(queryFollow.isFollower() ?
                        getString(R.string.title_follower_list, current == null ? "User" : current.getUserId()) :
                        getString(R.string.title_following_list, current == null ? "User" : current.getUserId()));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, UserListFragment.newInstance(queryFollow))
                        .commitNow();
            } else if (querySearchUser != null) {
                setTitle(getString(R.string.title_search_user_list, querySearchUser.getQuery()));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, UserListFragment.newInstance(querySearchUser))
                        .commitNow();
            }
        }
    }

    @Override
    public void onUserItemClicked(ItemUserBinding binding, UserInfo item) {
//        ProfileActivity.startWithTransition(this, binding, item);
        ProfileActivity.start(this, item);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed(); //액션바 홈 버튼 눌렀을 때 자신만 종료되도록
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
