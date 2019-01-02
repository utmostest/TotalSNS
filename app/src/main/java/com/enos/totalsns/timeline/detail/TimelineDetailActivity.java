package com.enos.totalsns.timeline.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.enos.totalsns.ContentsActivity;
import com.enos.totalsns.R;
import com.enos.totalsns.databinding.ActivityTimelineDetailBinding;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ContentsActivity}.
 */
public class TimelineDetailActivity extends AppCompatActivity {

    ActivityTimelineDetailBinding mDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline_detail);
        setSupportActionBar(mDataBinding.detailToolbar);

//        AppCompatUtils.setEnterCallback(this);

        mDataBinding.fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(TimelineDetailFragment.ITEM_ARTICLE, getIntent().getParcelableExtra(TimelineDetailFragment.ITEM_ARTICLE));
            TimelineDetailFragment fragment = new TimelineDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

//            navigateUpTo(new Intent(this, ContentsActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
