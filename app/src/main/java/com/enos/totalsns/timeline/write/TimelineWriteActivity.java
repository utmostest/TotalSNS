package com.enos.totalsns.timeline.write;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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
//            Log.i("observer", "isShouldClose onChanged called : " + shouldClose);
            if (shouldClose.compareAndSet(true, false)) finish();
        });
        mViewModel.getUploadingArticle().observe(this, article -> {
            if (article == null) {
                Toast.makeText(this, "포스팅 실패", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "포스팅 성공", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
