package com.enos.totalsns.timeline.detail;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.enos.totalsns.ContentsActivity;
import com.enos.totalsns.OnLoadLayoutListener;
import com.enos.totalsns.R;
import com.enos.totalsns.custom.autolink.AutoLinkMode;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.ActivityTimelineDetailBinding;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.profile.ProfileActivity;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.StringUtils;

import java.util.HashMap;

public class TimelineDetailActivity extends AppCompatActivity implements OnArticleClickListener, OnLoadLayoutListener {

    ActivityTimelineDetailBinding mDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline_detail);
        setSupportActionBar(mDataBinding.detailToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            ActivityCompat.postponeEnterTransition(this);
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
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArticleClicked(ItemArticleBinding binding, Article mItem, int position) {

    }

    @Override
    public void onArticleImageClicked(ImageView iv, Article article, int position) {

    }

    @Override
    public void onArticleProfileImgClicked(Article article) {
        ProfileActivity.start(this, article.getLongUserId());
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
                return;
            case MODE_PHONE:
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + matchedText));
                ActivityUtils.checkResolveAndStartActivity(intent, this);
                return;
            case MODE_EMAIL:
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{matchedText});
                ActivityUtils.checkResolveAndStartActivity(intent, this);
                return;
            case MODE_HASHTAG:
            case MODE_MENTION:
                ContentsActivity.startWithQuery(this, matchedText);
                finish();
                return;
        }
    }

    public static void startWithTransition(AppCompatActivity context, ItemArticleBinding binding, Article mItem, boolean enableImage) {
        if (enableImage) {
            start(context, mItem,
                    Pair.create(binding.tlProfileImg, context.getString(R.string.tran_profile_image)),
                    Pair.create(binding.tlUserName, context.getString(R.string.tran_user_name)),
                    Pair.create(binding.tlUserId, context.getString(R.string.tran_user_id)),
                    Pair.create(binding.tlTime, context.getString(R.string.tran_created_at)),
                    Pair.create(binding.tlMessage, context.getString(R.string.tran_message)),
                    Pair.create(binding.imageContainer, context.getString(R.string.tran_image_container)));
        } else {
            start(context, mItem,
                    Pair.create(binding.tlProfileImg, context.getString(R.string.tran_profile_image)),
                    Pair.create(binding.tlUserName, context.getString(R.string.tran_user_name)),
                    Pair.create(binding.tlUserId, context.getString(R.string.tran_user_id)),
                    Pair.create(binding.tlTime, context.getString(R.string.tran_created_at)),
                    Pair.create(binding.tlMessage, context.getString(R.string.tran_message))
            );
        }
    }

    public static void start(Context context, Article mItem) {
        Intent intent = new Intent(context, TimelineDetailActivity.class);
        intent.putExtra(TimelineDetailFragment.ITEM_ARTICLE, mItem);
        context.startActivity(intent);
    }

    public static void start(AppCompatActivity context, Article mItem, Pair<View, String>... pairs) {
        Intent intent = new Intent(context, TimelineDetailActivity.class);
        intent.putExtra(TimelineDetailFragment.ITEM_ARTICLE, mItem);
        ActivityUtils.startActivityWithTransition(context, intent, pairs);
    }

    @Override
    public void onLayoutLoaded() {
        ActivityCompat.startPostponedEnterTransition(this);
    }
}
