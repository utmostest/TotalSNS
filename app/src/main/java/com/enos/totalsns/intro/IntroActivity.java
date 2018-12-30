package com.enos.totalsns.intro;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
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
import com.enos.totalsns.databinding.ActivityIntroBinding;
import com.enos.totalsns.util.ViewModelFactory;

import java.util.concurrent.atomic.AtomicBoolean;

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

    private ActivityIntroBinding mDataBinding;

    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = () -> {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        mDataBinding.fullscreenContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
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

    private AtomicBoolean mHasActivityStarted = new AtomicBoolean(false);

    private IntroViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(this)).get(IntroViewModel.class);
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
        viewModel.getLoginResult().observe(this, result -> {
            if (result != null) {
                if (result.getLoginStatus() == LoginResult.STATUS_LOGIN_SUCCEED) {
                    finishAndStartActivity(TimelineActivity.class);
                } else {
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
        if (mHasActivityStarted.compareAndSet(false, true)) {
            finish();
            Intent intent = new Intent(this, activity);
            startActivity(intent);
        }
    }
}
