package com.enos.totalsns.timeline.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.MainThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.enos.totalsns.ContentsActivity;
import com.enos.totalsns.R;
import com.enos.totalsns.custom.autolink.AutoLinkMode;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.ActivityTimelineDetailBinding;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.image.ImageActivity;
import com.enos.totalsns.listener.OnArticleClickListener;
import com.enos.totalsns.listener.OnLoadLayoutListener;
import com.enos.totalsns.profile.ProfileActivity;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.StringUtils;

import java.util.HashMap;

public class TimelineDetailActivity extends AppCompatActivity implements OnArticleClickListener, OnLoadLayoutListener {

//    ActivityTimelineDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTimelineDetailBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline_detail);
        setSupportActionBar(mBinding.detailToolbar);
        setTitle(R.string.title_timeline_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
//            ActivityCompat.postponeEnterTransition(this);
            TimelineDetailFragment fragment = TimelineDetailFragment.newInstance(getIntent().getParcelableExtra(TimelineDetailFragment.ITEM_ARTICLE));
            getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed(); //액션바 홈 버튼 눌렀을 때 자신만 종료되도록
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArticleClicked(ItemArticleBinding binding, Article mItem) {

    }

    @Override
    public void onArticleImageClicked(ImageView iv, Article article, int position) {
        ImageActivity.start(this, article.getImageUrls(), position);
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

//    public static void startWithTransition(AppCompatActivity context, ItemArticleBinding binding, Article mItem, boolean enableImage) {
//
//        ArrayList<Pair<View, String>> pairList = new ArrayList<>();
//        pairList.add(Pair.create(binding.tlProfileImg, context.getString(R.string.tran_profile_image)));
//        pairList.add(Pair.create(binding.tlUserName, context.getString(R.string.tran_user_name)));
//        pairList.add(Pair.create(binding.tlUserId, context.getString(R.string.tran_user_id)));
//        pairList.add(Pair.create(binding.tlTime, context.getString(R.string.tran_created_at)));
//        pairList.add(Pair.create(binding.tlMessage, context.getString(R.string.tran_message)));
//        pairList.add(Pair.create(binding.imageContainer, context.getString(R.string.tran_image_container)));
//        Intent intent = new Intent(context, TimelineDetailActivity.class);
//        intent.putExtra(TimelineDetailFragment.ITEM_ARTICLE, mItem);
//        ActivityUtils.startActivityWithTransition(context, intent, pairList);
//    }

    public static void start(AppCompatActivity context, Article mItem) {
        Intent intent = new Intent(context, TimelineDetailActivity.class);
        intent.putExtra(TimelineDetailFragment.ITEM_ARTICLE, mItem);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }

    @MainThread
    @Override
    public void onLayoutLoaded() {
//        ActivityCompat.startPostponedEnterTransition(this);
    }
}
