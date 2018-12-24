package com.enos.totalsns.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.interfaces.OnTimelineResult;
import com.enos.totalsns.view.adapter.ArticleAdapter;
import com.enos.totalsns.viewmodel.SnsClientViewModel;

import java.util.List;

public class TimelineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SnsClientViewModel viewModel;

    // TODO 하단 네비게이션 뷰 추가 및 각종 화면 구현
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
//        FragmentManager fragmentManager = getSupportFragmentManager();

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

//        fragmentManager.beginTransaction().replace(R.id.main_frag_container, MainFragment.newInstance(menuType)).commit();

        return menuSelected;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        setTitle(R.string.title_activity_timeline);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewModel = ViewModelProviders.of(this).get(SnsClientViewModel.class);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            toggleFab(fab);
//            new Thread(() -> {
//                try {
//                    Status status = TwitterManager.getInstance().updateStatus(new Date().toString() + " 트윗 메시지 테스트");
//                    runOnUiThread(() -> Toast.makeText(TimelineActivity.this, status.getText(), Toast.LENGTH_SHORT).show());
//                } catch (TwitterException e) {
//                    runOnUiThread(() -> Toast.makeText(TimelineActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
//                    e.printStackTrace();
//                }
//            }).start();
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = findViewById(R.id.timeline_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initFragment();
    }

    private void toggleFab(FloatingActionButton fab) {
        if (fab.isShown()) {
            fab.hide();
            new Handler().postDelayed(() -> {
                toggleFab(fab);
            }, 1000);
        } else {
            fab.show();
        }
    }

    private void initFragment() {
        RecyclerView rv = findViewById(R.id.timeline_recylerview);
        rv.setLayoutManager(new LinearLayoutManager(this));
        viewModel.getHomeTimeline(new OnTimelineResult() {
            @Override
            public void onReceivedTimeline(List<Article> articleList) {
                ArticleAdapter adapter = new ArticleAdapter(articleList, (mItem, position) -> {
                    Toast.makeText(getBaseContext(), mItem.getMessage(), Toast.LENGTH_SHORT).show();
                });
                rv.setAdapter(adapter);
            }

            @Override
            public void onFailedReceiveTimeline(String message) {
                Toast.makeText(TimelineActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
//        getSupportFragmentManager().beginTransaction().add(R.id.main_frag_container, AccountFragment.newInstance(Constants.DEFAULT_SNS)).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START) || drawer.isDrawerVisible(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.START);
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

    @SuppressWarnings("StatementWithEmptyBody")
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        viewModel.signOut();
        finish();
        startActivity(new Intent(this, SelectSNSActivity.class));
    }
}