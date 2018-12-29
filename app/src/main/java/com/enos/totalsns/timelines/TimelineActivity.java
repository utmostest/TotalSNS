package com.enos.totalsns.timelines;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.accounts.AccountsActivity;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.ActivityTimelineBinding;
import com.enos.totalsns.timelinedetail.TimelineDetailActivity;
import com.enos.totalsns.timelinedetail.TimelineDetailFragment;
import com.enos.totalsns.timelinewrite.TimelineWriteActivity;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class TimelineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityTimelineBinding mDataBinding;
    private TimelineViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(this)).get(TimelineViewModel.class);

        initUI();
    }

    private void initUI() {
        setTitle(R.string.title_activity_timeline);
        setSupportActionBar(mDataBinding.appBar.toolbar);

        mDataBinding.appBar.content.swipeContainer.setOnRefreshListener(direction -> {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                viewModel.fetchRecentTimeline();
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                viewModel.fetchPastTimeline();
            }
        });

        mDataBinding.appBar.fab.setOnClickListener(view -> startTimelineWriteActivity());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDataBinding.drawerLayout, mDataBinding.appBar.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDataBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mDataBinding.navView.setNavigationItemSelectedListener(this);

        mDataBinding.appBar.timelineNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mDataBinding.appBar.content.tlRv.setLayoutManager(manager);
        TimelineAdapter adapter = new TimelineAdapter(null, (mItem, position) -> startTimelineDetailActivity(mItem));
        mDataBinding.appBar.content.tlRv.setAdapter(adapter);

        viewModel.getHomeTimeline().observe(this, articleList -> {
            if (articleList != null) {
                int currentPosFirst = manager.findFirstVisibleItemPosition();

                adapter.swapTimelineList(articleList);

                if (currentPosFirst == 0)
                    mDataBinding.appBar.content.tlRv.smoothScrollToPosition(0);
            } else {
                Toast.makeText(TimelineActivity.this, "timeline fetch failed", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.isNetworkOnUse().observe(this, refresh -> mDataBinding.appBar.content.swipeContainer.setRefreshing(refresh));
    }

    private void startTimelineWriteActivity() {
        Intent write = new Intent(this, TimelineWriteActivity.class);
        startActivity(write);
    }

    private void startTimelineDetailActivity(Article mItem) {
        Intent intent = new Intent(TimelineActivity.this, TimelineDetailActivity.class);
        intent.putExtra(TimelineDetailFragment.ITEM_ARTICLE, mItem);
        startActivity(intent);
    }

    private void signOut() {
        viewModel.signOut();
        finish();
        startActivity(new Intent(this, AccountsActivity.class));
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

        int menuType = Constants.DEFAULT_MENU;
        boolean menuSelected = false;

        switch (item.getItemId()) {
            case R.id.navigation_timeline:
                menuType = Constants.TIMELINE;
                menuSelected = true;
                break;
            case R.id.navigation_search:
                menuType = Constants.SEARCH;
                menuSelected = true;
                break;
            case R.id.navigation_notificate:
                menuType = Constants.NOTIFICATE;
                menuSelected = true;
                break;
            case R.id.navigation_direct:
                menuType = Constants.DIRECT_MSG;
                menuSelected = true;
                break;
        }

        return menuSelected;
    };
}