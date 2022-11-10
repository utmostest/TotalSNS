package com.enos.totalsns.timeline.write;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.enos.totalsns.R;
import com.enos.totalsns.databinding.ActivityTimelineWriteBinding;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ViewModelFactory;

public class TimelineWriteActivity extends AppCompatActivity {

    ActivityTimelineWriteBinding mBinding;
    TimelineWriteViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline_write);
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

    public static void start(AppCompatActivity context) {
        Intent write = new Intent(context, TimelineWriteActivity.class);
//        context.startActivity(write);
        ActivityUtils.startActivity(context,write);
    }
}
