package com.enos.totalsns.timeline.write;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enos.totalsns.R;
import com.enos.totalsns.databinding.ActivityTimelineWriteBinding;
import com.enos.totalsns.util.ViewModelFactory;

public class TimelineWriteActivity extends AppCompatActivity {

    ActivityTimelineWriteBinding mDataBinding;
    TimelineWriteViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline_write);
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(this)).get(TimelineWriteViewModel.class);

        initFragment(savedInstanceState);
        initObserver();
    }

    private void initFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TimelineWriteFragment.newInstance())
                    .commitNow();
        }
    }

    private void initObserver() {
        mViewModel.isShouldClose().observe(this, shouldClose -> {
            if (shouldClose == null) return;
            if (shouldClose.compareAndSet(true, false)) finish();
        });
        mViewModel.getUploadingArticle().observe(this, article -> {
            if (article != null) {
                finish();
            }
        });
    }

    public static void start(Context context) {
        Intent write = new Intent(context, TimelineWriteActivity.class);
        context.startActivity(write);
    }
}
