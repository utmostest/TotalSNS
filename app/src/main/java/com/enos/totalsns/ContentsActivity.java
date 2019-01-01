package com.enos.totalsns;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.enos.totalsns.accounts.AccountsActivity;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.databinding.ActivityContentsBinding;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.mention.MentionListFragment;
import com.enos.totalsns.message.list.MessageListFragment;
import com.enos.totalsns.message.list.OnMessageClickListener;
import com.enos.totalsns.search.OnSearchClickListener;
import com.enos.totalsns.search.SearchListFragment;
import com.enos.totalsns.timeline.detail.TimelineDetailActivity;
import com.enos.totalsns.timeline.detail.TimelineDetailFragment;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.timeline.list.TimelineListFragment;
import com.enos.totalsns.timeline.write.TimelineWriteActivity;
import com.enos.totalsns.util.AppCompatUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.ViewModelFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class ContentsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnArticleClickListener, OnSearchClickListener, OnMessageClickListener {

    private ActivityContentsBinding mDataBinding;
    private ContentsViewModel viewModel;
    private int menuType = Constants.DEFAULT_MENU;
    private AtomicBoolean mSignOutOnce = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_contents);
        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(this)).get(ContentsViewModel.class);

        initUI();
    }

    private void initUI() {
        initToolbar();
        initFab();
        initActionBar();
        initNavigation();
        initBottomNavigation();
        initFragment();
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.timeline_frag_container, TimelineListFragment.newInstance()).commit();
    }

    private void changeFragment(Class<?> clazz) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment current = fragmentManager.findFragmentByTag(clazz.getSimpleName());
        if (clazz.isAssignableFrom(TimelineListFragment.class)) {
            Fragment insert = current == null ? TimelineListFragment.newInstance() : current;
            fragmentManager.beginTransaction().replace(R.id.timeline_frag_container, insert, clazz.getSimpleName())
                    .addToBackStack(clazz.getSimpleName()).commit();
        } else if (clazz.isAssignableFrom(SearchListFragment.class)) {
            Fragment insert = current == null ? SearchListFragment.newInstance(1) : current;
            fragmentManager.beginTransaction().replace(R.id.timeline_frag_container, insert, clazz.getSimpleName())
                    .addToBackStack(clazz.getSimpleName()).commit();
        } else if (clazz.isAssignableFrom(MentionListFragment.class)) {
            Fragment insert = current == null ? MentionListFragment.newInstance() : current;
            fragmentManager.beginTransaction().replace(R.id.timeline_frag_container, insert, clazz.getSimpleName())
                    .addToBackStack(clazz.getSimpleName()).commit();
        } else if (clazz.isAssignableFrom(MessageListFragment.class)) {
            Fragment insert = current == null ? MessageListFragment.newInstance(1) : current;
            fragmentManager.beginTransaction().replace(R.id.timeline_frag_container, insert, clazz.getSimpleName())
                    .addToBackStack(clazz.getSimpleName()).commit();
        } else {
            throw new IllegalArgumentException(clazz.getSimpleName() + " doesn't exist in changeFragment");
        }
    }

    private void initToolbar() {
        setTitle(R.string.title_activity_timeline);
        setSupportActionBar(mDataBinding.appBar.toolbar);
    }

    private void initActionBar() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDataBinding.drawerLayout, mDataBinding.appBar.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDataBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initNavigation() {
        mDataBinding.navView.setNavigationItemSelectedListener(this);

        View header = mDataBinding.navView.getHeaderView(0);
        final TextView headerEmail = header.findViewById(R.id.header_email);
        final TextView headerName = header.findViewById(R.id.header_name);
        final ImageView headerProfile = header.findViewById(R.id.header_profile);
        final TextView followingNum = header.findViewById(R.id.header_following_num);
        final TextView followerNum = header.findViewById(R.id.header_follower_num);

        viewModel.getLoggedInUser().observe(this, user -> {
            if (user == null) return;

            headerEmail.setText(user.getEmail());
            headerName.setText(user.getName());
            followerNum.setText(String.valueOf(user.getFollowersCount()));
            followingNum.setText(String.valueOf(user.getFriendsCount()));

            Glide.with(this)
                    .load(user.get400x400ProfileImageURL())
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.drawable.ic_account_circle_black_48dp)
                                    .dontTransform()
                                    .optionalCircleCrop()
                    )
                    .transition(
                            new DrawableTransitionOptions()
                                    .crossFade(Constants.CROSS_FADE_MILLI)
                    )
                    .into(headerProfile);
        });
    }

    private void initBottomNavigation() {
        mDataBinding.appBar.timelineNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void initFab() {
        mDataBinding.appBar.fab.setOnClickListener(view -> startTimelineWriteActivity());
    }

    private void startTimelineDetailActivityWithImage(ItemArticleBinding binding, Article mItem, boolean enableImage) {
        AppCompatUtils.setExitCallback(this);
        if (enableImage) {
            startTimelineDetailActivity(mItem,
                    Pair.create(binding.tlProfileImg, getString(R.string.tran_profile_image)),
                    Pair.create(binding.tlUserName, getString(R.string.tran_user_name)),
                    Pair.create(binding.tlUserId, getString(R.string.tran_user_id)),
                    Pair.create(binding.tlTime, getString(R.string.tran_created_at)),
                    Pair.create(binding.tlMessage, getString(R.string.tran_message)),
                    Pair.create(binding.imageContainer, getString(R.string.tran_image_container)));
        } else {
            startTimelineDetailActivity(mItem,
                    Pair.create(binding.tlProfileImg, getString(R.string.tran_profile_image)),
                    Pair.create(binding.tlUserName, getString(R.string.tran_user_name)),
                    Pair.create(binding.tlUserId, getString(R.string.tran_user_id)),
                    Pair.create(binding.tlTime, getString(R.string.tran_created_at)),
                    Pair.create(binding.tlMessage, getString(R.string.tran_message))
            );
        }
    }

    private void startTimelineWriteActivity() {
        Intent write = new Intent(this, TimelineWriteActivity.class);
        startActivity(write);
    }

    private void startTimelineDetailActivity(Article mItem) {
        Intent intent = new Intent(ContentsActivity.this, TimelineDetailActivity.class);
        intent.putExtra(TimelineDetailFragment.ITEM_ARTICLE, mItem);
        startActivity(intent);
    }

    private void startTimelineDetailActivity(Article mItem, Pair<View, String>... pairs) {
        Intent intent = new Intent(ContentsActivity.this, TimelineDetailActivity.class);
        intent.putExtra(TimelineDetailFragment.ITEM_ARTICLE, mItem);
        AppCompatUtils.startActivityWithTransition(this, intent, pairs);
    }

    private void signOut() {
        if (mSignOutOnce.compareAndSet(false, true)) {
            viewModel.signOut();
            viewModel.isSignOutFinished().observe(this, (isFinished) -> {
                finish();
                startActivity(new Intent(this, AccountsActivity.class));
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mDataBinding.drawerLayout.isDrawerOpen(GravityCompat.START) || mDataBinding.drawerLayout.isDrawerVisible(GravityCompat.END)) {
            mDataBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean menuSelected = false;

        if (id == R.id.nav_dr_all) {
            menuSelected = true;
        } else if (id == R.id.nav_dr_selected) {
            menuSelected = true;
        } else if (id == R.id.nav_dr_twitter) {
            menuSelected = true;
        } else if (id == R.id.nav_dr_facebook) {
            menuSelected = true;
        } else if (id == R.id.nav_dr_instagram) {
            menuSelected = true;
        } else if (id == R.id.nav_dr_edit) {

        } else if (id == R.id.nav_dr_nearby) {

        } else if (id == R.id.nav_dr_setting) {

        } else if (id == R.id.nav_dr_sign_out) {
            signOut();
        }

        mDataBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return menuSelected;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        boolean menuSelected = false;
        Class<?> clazz = null;

        switch (item.getItemId()) {
            case R.id.navigation_timeline:
                menuType = Constants.TIMELINE;
                menuSelected = true;
                clazz = TimelineListFragment.class;
                break;
            case R.id.navigation_search:
                menuType = Constants.SEARCH;
                menuSelected = true;
                clazz = SearchListFragment.class;
                break;
            case R.id.navigation_notificate:
                menuType = Constants.INFO;
                menuSelected = true;
                clazz = MentionListFragment.class;
                break;
            case R.id.navigation_direct:
                menuType = Constants.DIRECT_MSG;
                menuSelected = true;
                clazz = MessageListFragment.class;
                break;
        }

        if (menuSelected && clazz != null) {
            changeFragment(clazz);
        }

        return menuSelected;
    };

    @Override
    public void onArticleClicked(Article mItem, int position) {
//            boolean enableImage = false;
//            if (mItem.getImageUrls() != null && mItem.getImageUrls().length > 0) enableImage = true;
        startTimelineDetailActivity(mItem);
//            startTimelineDetailActivityWithImage(binding, mItem, enableImage);
    }

    @Override
    public void onArticleImageClicked(ImageView iv, Article article, int position) {
        if (article != null) {
            SingletonToast.getInstance().show(article.getImageUrls()[position] + position, Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onMessageClicked(Message item) {
        SingletonToast.getInstance().show(item + "", Toast.LENGTH_SHORT);
    }

    @Override
    public void onSearchItemClicked(com.enos.totalsns.search.dummy.DummyContent.DummyItem item) {
        SingletonToast.getInstance().show(item.id + "" + item.content + "\n" + item.details, Toast.LENGTH_SHORT);
    }
}