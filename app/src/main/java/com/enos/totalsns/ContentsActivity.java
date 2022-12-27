package com.enos.totalsns;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.enos.totalsns.accounts.AccountsActivity;
import com.enos.totalsns.custom.autolink.AutoLinkMode;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ActivityContentsBinding;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.databinding.ItemSearchUserBinding;
import com.enos.totalsns.databinding.ItemUserBinding;
import com.enos.totalsns.image.ImageActivity;
import com.enos.totalsns.listener.OnArticleClickListener;
import com.enos.totalsns.listener.OnFollowListener;
import com.enos.totalsns.listener.OnMessageClickListener;
import com.enos.totalsns.listener.OnMoreUserButtonClickListener;
import com.enos.totalsns.listener.OnSearchUserClickListener;
import com.enos.totalsns.listener.OnUserClickListener;
import com.enos.totalsns.mention.MentionListFragment;
import com.enos.totalsns.message.detail.MessageDetailActivity;
import com.enos.totalsns.message.list.MessageListFragment;
import com.enos.totalsns.message.send.MessageSendActivity;
import com.enos.totalsns.nearby.NearbyArticleActivity;
import com.enos.totalsns.profile.ProfileActivity;
import com.enos.totalsns.search.SearchListFragment;
import com.enos.totalsns.settings.SettingsActivity;
import com.enos.totalsns.timeline.detail.TimelineDetailActivity;
import com.enos.totalsns.timeline.list.TimelineListFragment;
import com.enos.totalsns.timeline.write.TimelineWriteActivity;
import com.enos.totalsns.userlist.UserListActivity;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ColorUtils;
import com.enos.totalsns.util.CompareUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.StringUtils;
import com.enos.totalsns.util.ViewModelFactory;
import com.ferfalk.simplesearchview.SimpleSearchView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContentsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnArticleClickListener,
        OnFollowListener, OnMessageClickListener, OnUserClickListener,
        OnMoreUserButtonClickListener, OnSearchUserClickListener {

    private ActivityContentsBinding mBinding;
    private ContentsViewModel viewModel;
    private int menuType = Constants.DEFAULT_MENU;
    private AtomicBoolean mSignOutOnce = new AtomicBoolean(false);
    private AtomicBoolean mQuitOnce = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contents);
        viewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(this)).get(ContentsViewModel.class);

        initUI(savedInstanceState);
        initObserver();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (mBinding.appBar.timelineNavigation.getSelectedItemId() != R.id.navigation_search) {
                mBinding.appBar.timelineNavigation.setSelectedItemId(R.id.navigation_search);
            }
            viewModel.getSearchQuery().postValue(query);
        }
    }

    private void initUI(Bundle saved) {
        initToolbar();
        initSearchView();
        initFab();
        initActionBar();
        initNavigation();
        initBottomNavigation();
        if (saved == null) {
            initFragment();
        } else {
            setIconAndShowFab();
        }
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
            mBinding.appBar.toolbarTitle.setText(R.string.title_timeline);
        } else if (clazz.isAssignableFrom(SearchListFragment.class)) {
            Fragment insert = current == null ? SearchListFragment.newInstance() : current;
            fragmentManager.beginTransaction().replace(R.id.timeline_frag_container, insert, clazz.getSimpleName())
                    .addToBackStack(clazz.getSimpleName()).commit();
            mBinding.appBar.toolbarTitle.setText(R.string.title_search);
        } else if (clazz.isAssignableFrom(MentionListFragment.class)) {
            Fragment insert = current == null ? MentionListFragment.newInstance() : current;
            fragmentManager.beginTransaction().replace(R.id.timeline_frag_container, insert, clazz.getSimpleName())
                    .addToBackStack(clazz.getSimpleName()).commit();
            mBinding.appBar.toolbarTitle.setText(R.string.title_my_post);
        } else if (clazz.isAssignableFrom(MessageListFragment.class)) {
            Fragment insert = current == null ? MessageListFragment.newInstance() : current;
            fragmentManager.beginTransaction().replace(R.id.timeline_frag_container, insert, clazz.getSimpleName())
                    .addToBackStack(clazz.getSimpleName()).commit();
            mBinding.appBar.toolbarTitle.setText(R.string.title_direct);
        } else {
            throw new IllegalArgumentException(clazz.getSimpleName() + " doesn't exist in changeFragment");
        }
    }

    private void initToolbar() {
        setTitle("");
        mBinding.appBar.toolbarTitle.setText(R.string.title_timeline);
        setSupportActionBar(mBinding.appBar.toolbar);
    }

    private void initSearchView() {
        mBinding.appBar.searchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && query.length() > 0) {
                    viewModel.getSearchQuery().postValue(query);
                } else {
                    SingletonToast.getInstance().log("검색어를 입력하세요");
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

        mBinding.appBar.searchView.setOnSearchViewListener(new SimpleSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                if (mBinding.appBar.timelineNavigation.getSelectedItemId() != R.id.navigation_search) {
                    mBinding.appBar.timelineNavigation.setSelectedItemId(R.id.navigation_search);
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
                this, mBinding.drawerLayout, mBinding.appBar.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.icon_tint));
        mBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initNavigation() {
        mBinding.navView.setNavigationItemSelectedListener(this);

        viewModel.getLoggedInUser().observe(this, this::updateHeaderView);
        viewModel.getUserCache().observe(this, cache -> {
            UserInfo user = viewModel.getLoggedInUser().getValue();
            if (user == null || cache == null) return;
            Log.i("userCache", cache.size() + " contents");
            UserInfo current = cache.get(user.getLongUserId());
            if (!CompareUtils.isUserInfoEqual(user, current)) {
                updateHeaderView(current);
            }
        });
    }

    private void updateHeaderView(UserInfo user) {
        if (user == null) return;
        View header = mBinding.navView.getHeaderView(0);
        final TextView headerUserid = header.findViewById(R.id.header_userid);
        final TextView headerName = header.findViewById(R.id.header_name);
        final ImageView headerProfile = header.findViewById(R.id.header_profile);
        final TextView followingNum = header.findViewById(R.id.header_following_num);
        final TextView followerNum = header.findViewById(R.id.header_follower_num);
        final TextView followingLabel = header.findViewById(R.id.header_following_label);
        final TextView followerLabel = header.findViewById(R.id.header_follower_label);
        final ImageView headerBackground = header.findViewById(R.id.header_background);
        final View headerTextBg = header.findViewById(R.id.header_text_bg);

        headerUserid.setText(user.getUserId());
        headerName.setText(user.getUserName());
        followerNum.setText(String.valueOf(user.getFollowerCount()));
        followingNum.setText(String.valueOf(user.getFollowingCount()));

        followingLabel.setOnClickListener(v -> UserListActivity.startFollowList(this, user.getLongUserId(), false));
        followingNum.setOnClickListener(v -> UserListActivity.startFollowList(this, user.getLongUserId(), false));
        followerLabel.setOnClickListener(v -> UserListActivity.startFollowList(this, user.getLongUserId(), true));
        followerNum.setOnClickListener(v -> UserListActivity.startFollowList(this, user.getLongUserId(), true));
        headerProfile.setOnClickListener(v -> ProfileActivity.start(this, user));
        GlideUtils.loadProfileImage(this, user.getProfileImg(), headerProfile);
        String profileBackImg = user.getProfileBackImg();
        if (StringUtils.isStringValid(profileBackImg)) {
//            headerBackground.setOnClickListener(v -> ImageActivity.start(this, new String[]{profileBackImg}, 0));

            GlideUtils.loadBackImageWithCallback(this, profileBackImg, headerBackground,
                    new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (resource != null) {
//                                setTextColor(ColorUtils.getBodyTextColorFromPalette(resource), headerEmail, headerName, followerNum, followerLabel, followingNum, followingLabel);
                            }
                            return false;
                        }
                    });
        } else if (StringUtils.isStringValid(user.getProfileBackColor())) {
            headerBackground.setImageDrawable(null);
            int backGround = Color.parseColor("#" + user.getProfileBackColor());
            headerBackground.setBackgroundColor(backGround);
            headerTextBg.setBackgroundColor(backGround);
            int textColor = ColorUtils.getComplimentColor(backGround);
            setTextColor(textColor, headerUserid, headerName, followerNum, followerLabel, followingNum, followingLabel);
        }
    }

    private void setTextColor(int color, TextView... textViews) {
        if (textViews != null) {
            for (TextView tv : textViews) {
                tv.setTextColor(color);
            }
        }
    }

    private void initBottomNavigation() {
        mBinding.appBar.timelineNavigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void initFab() {
        mBinding.appBar.fab.setOnClickListener(view -> TimelineWriteActivity.start(this));
    }

    private void initObserver() {
        viewModel.isSignOutFinished().observe(this, (isFinished) -> {
            if (isFinished != null && isFinished) {
                if (mSignOutOnce.compareAndSet(false, true)) {
                    AccountsActivity.start(this);
                    finish();
                }
            }
        });
        viewModel.sholudQuit().observe(this, (shouldQuit) -> {
            if (shouldQuit != null && shouldQuit) {
                if (mQuitOnce.compareAndSet(false, true)) {
                    finish();
                }
            }
        });
    }

    private void signOut() {
        viewModel.signOut();
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START) || mBinding.drawerLayout.isDrawerVisible(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (mBinding.appBar.searchView.onBackPressed()) {
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
        if (mBinding != null)
            mBinding.appBar.searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            edit();
        } else if (id == R.id.nav_dr_nearby) {
            NearbyArticleActivity.start(this);
        } else if (id == R.id.nav_dr_setting) {
            SettingsActivity.start(this);
        } else if (id == R.id.nav_dr_sign_out) {
            signOut();
        }

        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return menuSelected;
    }

    private void edit() {
    }

    private NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
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
            case R.id.navigation_to_me:
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

        if (menuSelected) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.timeline_frag_container);

            if (currentFragment != null) {
                String current = currentFragment.getClass().getSimpleName();
                boolean isCurrentDm = current.equals(MessageListFragment.class.getSimpleName());
                boolean isRecentDm = clazz.getSimpleName().equals(MessageListFragment.class.getSimpleName());
                if ((isCurrentDm || isRecentDm) && !clazz.getSimpleName().equals(current)) {
                    hideFabDelayedShow();
                }
            }
            changeFragment(clazz);
        }

        return menuSelected;
    };

    private void hideFabDelayedShow() {
        hideFab();
        handler.removeCallbacks(mRunnable);
        handler.postDelayed(mRunnable, Constants.FAB_DELAY_MILLI);
    }

    private final Handler handler = new Handler();

    private Runnable mRunnable = () -> runOnUiThread(this::setIconAndShowFab);

    private void hideFab() {
        mBinding.appBar.fab.hide();
    }

    private void setIconAndShowFab() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.timeline_frag_container);
        int resource = R.drawable.ic_add_white_24dp;
        if (currentFragment != null) {
            String current = currentFragment.getClass().getSimpleName();
            if (current.equals(MessageListFragment.class.getSimpleName())) {
                resource = R.drawable.ic_chat_white_24dp;
                mBinding.appBar.fab.setOnClickListener(view -> MessageSendActivity.start(this));
            } else {
                mBinding.appBar.fab.setOnClickListener(view -> TimelineWriteActivity.start(this));
            }
        }
        mBinding.appBar.fab.setImageResource(resource);
        mBinding.appBar.fab.show();
    }

    @Override
    public void onArticleClicked(ItemArticleBinding binding, Article mItem) {
//        TimelineDetailActivity.startWithTransition(this, binding, mItem, StringUtils.getActualSize(mItem.getImageUrls()) > 0);
        TimelineDetailActivity.start(this, mItem);
    }

    @Override
    public void onArticleImageClicked(ImageView iv, Article article, int position) {
        ImageActivity.start(this, article.getImageUrls(), position);
    }

    @Override
    public void onArticleProfileImgClicked(Article article) {
        if (article != null) {
            ProfileActivity.start(this, article.getLongUserId());
        }
    }

    @Override
    public void onAutoLinkClicked(AutoLinkMode autoLinkMode, String text, HashMap<String, String> hashMap) {
        String matchedText = StringUtils.removeUnnecessaryString(text);

        SingletonToast.getInstance().log(autoLinkMode + " : " + matchedText);
        Intent intent = new Intent();
        switch (autoLinkMode) {
            case MODE_URL:
                String normalizedString = StringUtils.getExpandedUrlFromMap(hashMap, matchedText);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(normalizedString));
                ActivityUtils.checkResolveAndStartActivity(intent, this);
                break;
            case MODE_PHONE:
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + matchedText));
                ActivityUtils.checkResolveAndStartActivity(intent, this);
                break;
            case MODE_EMAIL:
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{matchedText});
                ActivityUtils.checkResolveAndStartActivity(intent, this);
                break;
            case MODE_HASHTAG:
            case MODE_MENTION:
                mBinding.appBar.timelineNavigation.setSelectedItemId(R.id.navigation_search);
                viewModel.getSearchQuery().postValue(text);
                break;
        }
    }

    @Override
    public void onMessageClicked(Message item) {
        UserInfo tempUser = new UserInfo(item);
        MessageDetailActivity.start(this, tempUser);
    }

    @Override
    public void onUserItemClicked(ItemUserBinding binding, UserInfo item) {
        Log.i("layout", "onSearchUserItemClicked");
//        ProfileActivity.startWithTransition(this, binding, item);
        ProfileActivity.start(this, item);
    }

    @Override
    public void onSearchUserItemClicked(ItemSearchUserBinding binding, UserInfo item) {
        Log.i("layout", "onSearchUserItemClicked");
//        ProfileActivity.startWithTransition(this, binding, item);
        ProfileActivity.start(this, item);
    }

    @Override
    public void onFollowTextClicked(UserInfo info, boolean isFollower) {
        if (info.isProtected()) {
            Toast.makeText(this, "This user is protected", Toast.LENGTH_SHORT).show();
        } else {
            UserListActivity.startFollowList(this, info.getLongUserId(), isFollower);
        }
    }

    @Override
    public void onMoreUserButtonClicked() {
        String query = viewModel.getSearchQuery().getValue();
        if (query != null && query.length() > 0) {
            UserListActivity.startUserList(this, query);
        }
    }

    public static void start(AppCompatActivity context) {
        Intent intent = new Intent(context, ContentsActivity.class);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }

    public static void startWithQuery(AppCompatActivity context, String query) {
        Intent intent = new Intent(context, ContentsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }
}