package com.enos.totalsns;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.enos.totalsns.accounts.AccountsActivity;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ActivityContentsBinding;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.mention.MentionListFragment;
import com.enos.totalsns.message.detail.MessageDetailActivity;
import com.enos.totalsns.message.detail.MessageDetailFragment;
import com.enos.totalsns.message.list.MessageListFragment;
import com.enos.totalsns.message.list.OnMessageClickListener;
import com.enos.totalsns.profile.ProfileActivity;
import com.enos.totalsns.profile.ProfileFragment;
import com.enos.totalsns.search.OnUserClickListener;
import com.enos.totalsns.search.SearchListFragment;
import com.enos.totalsns.timeline.detail.TimelineDetailActivity;
import com.enos.totalsns.timeline.detail.TimelineDetailFragment;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.timeline.list.TimelineListFragment;
import com.enos.totalsns.timeline.write.TimelineWriteActivity;
import com.enos.totalsns.util.AppCompatUtils;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.ViewModelFactory;
import com.ferfalk.simplesearchview.SimpleSearchView;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContentsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnArticleClickListener, OnUserClickListener, OnMessageClickListener {

    private ActivityContentsBinding mDataBinding;
    private ContentsViewModel viewModel;
    private int menuType = Constants.DEFAULT_MENU;
    private AtomicBoolean mSignOutOnce = new AtomicBoolean(false);
    private AtomicBoolean mQuitOnce = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_contents);
        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(this)).get(ContentsViewModel.class);

        initUI();
        initObserver();
    }

    private void initUI() {
        initToolbar();
        initSearchView();
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
            Fragment insert = current == null ? SearchListFragment.newInstance() : current;
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

    private void initSearchView() {
        mDataBinding.appBar.searchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && query.length() > 0) {
                    viewModel.getSearchQuery().postValue(query);
                } else {
                    SingletonToast.getInstance().show("검색어를 입력하세요");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextCleared() {
                return false;
            }
        });

        mDataBinding.appBar.searchView.setOnSearchViewListener(new SimpleSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                if (mDataBinding.appBar.timelineNavigation.getSelectedItemId() != R.id.navigation_search) {
                    mDataBinding.appBar.timelineNavigation.setSelectedItemId(R.id.navigation_search);
                }
            }

            @Override
            public void onSearchViewClosed() {
            }

            @Override
            public void onSearchViewShownAnimation() {
            }

            @Override
            public void onSearchViewClosedAnimation() {
            }
        });
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
        final TextView followingLabel = header.findViewById(R.id.header_following_label);
        final TextView followerLabel = header.findViewById(R.id.header_follower_label);
        final ImageView headerBackground = header.findViewById(R.id.header_background);

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
            String profileBackImg = user.getProfileBackgroundImageURL();
            if (ConvertUtils.isStringValid(profileBackImg)) {
                Glide.with(this)
                        .asBitmap()
                        .load(profileBackImg)
                        .apply(
                                new RequestOptions()
                                        .placeholder(R.drawable.side_nav_bar)
                                        .dontTransform()
                                        .centerCrop()
                        )
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                if (resource != null) {
                                    Palette p = Palette.from(resource).generate();
                                    List<Palette.Swatch> swatches = p.getSwatches();
                                    if (swatches != null && swatches.size() > 0) {
                                        Palette.Swatch s = swatches.get(0);
                                        headerEmail.setTextColor(s.getTitleTextColor());
                                        headerName.setTextColor(s.getTitleTextColor());
                                        followerNum.setTextColor(s.getBodyTextColor());
                                        followingNum.setTextColor(s.getBodyTextColor());
                                        followerLabel.setTextColor(s.getBodyTextColor());
                                        followingLabel.setTextColor(s.getBodyTextColor());
                                    }
                                }
                                return false;
                            }
                        })
                        .into(headerBackground);
            } else if (ConvertUtils.isStringValid(user.getProfileBackgroundColor())) {
                headerBackground.setImageDrawable(null);
                headerBackground.setBackgroundColor(Color.parseColor("#" + user.getProfileBackgroundColor()));
            } else {
                headerBackground.setImageResource(R.drawable.side_nav_bar);
            }
        });
    }

    private void initBottomNavigation() {
        mDataBinding.appBar.timelineNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void initFab() {
        mDataBinding.appBar.fab.setOnClickListener(view -> startTimelineWriteActivity());
    }

    private void initObserver() {
        viewModel.isSignOutFinished().observe(this, (isFinished) -> {
            if (isFinished) {
                if (mSignOutOnce.compareAndSet(false, true)) {
                    finish();
                    startActivity(new Intent(this, AccountsActivity.class));
                }
            }
        });
        viewModel.sholudQuit().observe(this, (shouldQuit) -> {
            if (shouldQuit) {
                if (mQuitOnce.compareAndSet(false, true)) {
                    finish();
                }
            }
        });
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

    private void startDirectMessageDetailActivity(long senderId) {
        Intent intent = new Intent(ContentsActivity.this, MessageDetailActivity.class);
        intent.putExtra(MessageDetailFragment.COLUMN_SENDER_ID, senderId);
        startActivity(intent);
    }

    private void startProfileActivity(long userId) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileFragment.ARG_USER_ID, userId);
        startActivity(intent);
    }

    private void signOut() {
        viewModel.signOut();
    }

    @Override
    public void onBackPressed() {
        if (mDataBinding.appBar.searchView.onBackPressed()) {
            return;
        } else if (mDataBinding.drawerLayout.isDrawerOpen(GravityCompat.START) || mDataBinding.drawerLayout.isDrawerVisible(GravityCompat.END)) {
            mDataBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            viewModel.onBackPressed();
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        if (mDataBinding != null)
            mDataBinding.appBar.searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
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
        startTimelineDetailActivity(mItem);
    }

    @Override
    public void onArticleImageClicked(ImageView iv, Article article, int position) {
        if (article != null) {
            startProfileActivity(article.getLongUserId());
        }
    }

    @Override
    public void onMessageClicked(Message item) {
        startDirectMessageDetailActivity(item.getSenderTableId());
    }

    @Override
    public void onUserItemClicked(UserInfo item) {
        startProfileActivity(item.getLongUserId());
    }

    @Override
    public void onFollowButtonClicked(UserInfo info) {
        SingletonToast.getInstance().show("onFollowButtonClicked",
                info.getUserId() + "" + info.getUserName() + "\n" + info.getMessage(), Toast.LENGTH_SHORT);
    }
}