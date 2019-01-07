package com.enos.totalsns.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.userlist.OnFollowListener;
import com.enos.totalsns.userlist.UserListActivity;
import com.enos.totalsns.userlist.UserListFragment;

public class ProfileActivity extends AppCompatActivity implements OnFollowListener, OnArticleClickListener {

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
        UserInfo userInfo = getIntent().getParcelableExtra(ProfileFragment.ARG_USER_INFO);
        long userId = getIntent().getLongExtra(ProfileFragment.ARG_USER_ID, ProfileFragment.INVALID_ID);
        if (userInfo != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.container, ProfileFragment.newInstance(userInfo)).commitNow();
        else if (userId > ProfileFragment.INVALID_ID)
            getSupportFragmentManager().beginTransaction().replace(R.id.container, ProfileFragment.newInstance(userId)).commitNow();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startFollowActivity(long userId, boolean isFollower) {
        QueryFollow queryFollow = new QueryFollow(QueryFollow.FIRST, userId, -1, isFollower);
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra(UserListFragment.ARG_QUERY_FOLLOW, queryFollow);
        startActivity(intent);
    }

    @Override
    public void onFollowTextClicked(UserInfo info, boolean isFollower) {
        startFollowActivity(info.getLongUserId(), isFollower);
    }

    @Override
    public void onArticleClicked(ItemArticleBinding binding, Article mItem, int position) {

    }

    @Override
    public void onArticleImageClicked(ImageView iv, Article article, int position) {

    }

    @Override
    public void onArticleProfileImgClicked(Article article) {

    }
}
