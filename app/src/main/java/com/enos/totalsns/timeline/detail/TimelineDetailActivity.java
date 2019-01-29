package com.enos.totalsns.timeline.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.enos.totalsns.ContentsActivity;
import com.enos.totalsns.listener.OnLoadLayoutListener;
import com.enos.totalsns.R;
import com.enos.totalsns.custom.autolink.AutoLinkMode;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.ActivityTimelineDetailBinding;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.profile.ProfileActivity;
import com.enos.totalsns.listener.OnArticleClickListener;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class TimelineDetailActivity extends AppCompatActivity implements OnArticleClickListener, OnLoadLayoutListener {

    ActivityTimelineDetailBinding mDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline_detail);
        setSupportActionBar(mDataBinding.detailToolbar);
        setTitle(R.string.title_timeline_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            ActivityCompat.postponeEnterTransition(this);
            TimelineDetailFragment fragment = TimelineDetailFragment.newInstance(getIntent().getParcelableExtra(TimelineDetailFragment.ITEM_ARTICLE));
            getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArticleClicked(ItemArticleBinding binding, Article mItem) {

    }

    @Override
    public void onArticleImageClicked(ImageView iv, Article article, int position) {
        Toast.makeText(this, "TODO : add image pager activity", Toast.LENGTH_SHORT).show();
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

        ArrayList<Pair<View, String>> pairList = new ArrayList<>();
        pairList.add(Pair.create(binding.tlProfileImg, context.getString(R.string.tran_profile_image)));
        pairList.add(Pair.create(binding.tlUserName, context.getString(R.string.tran_user_name)));
        pairList.add(Pair.create(binding.tlUserId, context.getString(R.string.tran_user_id)));
        pairList.add(Pair.create(binding.tlTime, context.getString(R.string.tran_created_at)));
        pairList.add(Pair.create(binding.tlMessage, context.getString(R.string.tran_message)));
        pairList.add(Pair.create(binding.imageContainer, context.getString(R.string.tran_image_container)));
        Intent intent = new Intent(context, TimelineDetailActivity.class);
        intent.putExtra(TimelineDetailFragment.ITEM_ARTICLE, mItem);
        ActivityUtils.startActivityWithTransition(context, intent, pairList);
    }

    public static void start(AppCompatActivity context, Article mItem) {
        Intent intent = new Intent(context, TimelineDetailActivity.class);
        intent.putExtra(TimelineDetailFragment.ITEM_ARTICLE, mItem);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }

    @MainThread
    @Override
    public void onLayoutLoaded() {
        ActivityCompat.startPostponedEnterTransition(this);
    }
}
