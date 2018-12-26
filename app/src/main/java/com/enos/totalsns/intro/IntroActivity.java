package com.enos.totalsns.intro;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.accounts.AccountsActivity;
import com.enos.totalsns.timelines.TimelineActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IntroActivity extends AppCompatActivity {

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final int START_ACTIVITY_DELAY = 1000;

    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = () -> {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        startActivityDelayed(START_ACTIVITY_DELAY);
    };

    private final Runnable mHideRunnable = this::hide;

    private final Handler mActivityHandler = new Handler(Looper.myLooper());
    private final Runnable mStartActivityRunnalbe = this::attemptLogin;

    private IntroViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        viewModel = ViewModelProviders.of(this).get(IntroViewModel.class);

        mContentView = findViewById(R.id.fullscreen_content);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    protected void onDestroy() {
        removeCallbacks();
        super.onDestroy();
    }

    private void removeCallbacks() {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mActivityHandler.removeCallbacks(mStartActivityRunnalbe);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void attemptLogin() {
        viewModel.attempCurrentAccoutsLogin(this);
        viewModel.getLoginResultList().observe(this, result -> {
            if (result != null) {
                if (result.isLoginSucced()) {
                    finishAndStartActivity(TimelineActivity.class);
                } else {
                    Toast.makeText(IntroActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    finishAndStartActivity(AccountsActivity.class);
                }
            }
        });
    }

    private void startActivityDelayed(int delayMillis) {
        mActivityHandler.removeCallbacks(mStartActivityRunnalbe);
        mActivityHandler.postDelayed(mStartActivityRunnalbe, delayMillis);
    }

    private void finishAndStartActivity(Class<?> activity) {
        finish();
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}
