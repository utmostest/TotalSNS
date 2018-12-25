package com.enos.totalsns.timelinewrite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enos.totalsns.R;

public class TimelineWriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_write);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TimelineWriteFragment.newInstance())
                    .commitNow();
        }
    }
}
