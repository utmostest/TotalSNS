package com.enos.totalsns.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.enos.totalsns.ContentsActivity;
import com.enos.totalsns.OnLoadLayoutListener;
import com.enos.totalsns.R;
import com.enos.totalsns.custom.autolink.AutoLinkMode;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.databinding.ItemSearchUserBinding;
import com.enos.totalsns.databinding.ItemUserBinding;
import com.enos.totalsns.timeline.detail.TimelineDetailActivity;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.userlist.OnFollowListener;
import com.enos.totalsns.userlist.UserListActivity;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements OnFollowListener, OnArticleClickListener, OnLoadLayoutListener {

    long userId = ProfileFragment.INVALID_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (savedInstanceState == null) {
            initFragment();
        }
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initFragment() {
        UserInfo userInfo = getIntent().getParcelableExtra(ProfileFragment.ARG_USER_INFO);
        userId = getIntent().getLongExtra(ProfileFragment.ARG_USER_ID, ProfileFragment.INVALID_ID);
        if (userInfo != null) {
            Log.i("layout", "load started");
            //이미지가 로드될때까지 트랜지션 연기
            ActivityCompat.postponeEnterTransition(this);
            getSupportFragmentManager().beginTransaction().add(R.id.container, ProfileFragment.newInstance(userInfo)).commitNow();
        } else if (userId > ProfileFragment.INVALID_ID)
            getSupportFragmentManager().beginTransaction().add(R.id.container, ProfileFragment.newInstance(userId)).commitNow();
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
    public void onFollowTextClicked(UserInfo info, boolean isFollower) {
        if (info.isProtected()) {
            Toast.makeText(this, "This user is protected", Toast.LENGTH_SHORT).show();
        } else {
            UserListActivity.startFollowList(this, info.getLongUserId(), isFollower);
        }
    }

    @Override
    public void onArticleClicked(ItemArticleBinding binding, Article mItem) {
        TimelineDetailActivity.startWithTransition(this, binding, mItem, StringUtils.getActualSize(mItem.getImageUrls()) > 0);
    }

    @Override
    public void onArticleImageClicked(ImageView iv, Article article, int position) {

    }

    @Override
    public void onArticleProfileImgClicked(Article article) {
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

    public static void start(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileFragment.ARG_USER_INFO, userInfo);
        context.startActivity(intent);
    }

    public static void start(Context context, long longUserId) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileFragment.ARG_USER_ID, longUserId);
        context.startActivity(intent);
    }

    public static void startWithTransition(AppCompatActivity context, ItemUserBinding binding, UserInfo mItem) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileFragment.ARG_USER_INFO, mItem);
        ArrayList<Pair<View, String>> pairs = new ArrayList<>();
        pairs.add(Pair.create(binding.fProfileImg, context.getString(R.string.tran_profile_image_u)));
        pairs.add(Pair.create(binding.fFollowBtn, context.getString(R.string.tran_follow_btn)));
        pairs.add(Pair.create(binding.fUserName, context.getString(R.string.tran_user_name_u)));
        pairs.add(Pair.create(binding.fUserId, context.getString(R.string.tran_user_id_u)));
        pairs.add(Pair.create(binding.fMessage, context.getString(R.string.tran_message_u)));
        ActivityUtils.startActivityWithTransition(context, intent, pairs);
    }

    public static void startWithTransition(AppCompatActivity context, ItemSearchUserBinding binding, UserInfo mItem) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileFragment.ARG_USER_INFO, mItem);
        ArrayList<Pair<View, String>> pairs = new ArrayList<>();
        pairs.add(Pair.create(binding.itemUserProfile, context.getString(R.string.tran_profile_image_u)));
        pairs.add(Pair.create(binding.itemUserProfileBack, context.getString(R.string.tran_profile_back)));
        pairs.add(Pair.create(binding.itemUserFollowBtn, context.getString(R.string.tran_follow_btn)));
        pairs.add(Pair.create(binding.itemUserName, context.getString(R.string.tran_user_name_u)));
        pairs.add(Pair.create(binding.itemUserScreenId, context.getString(R.string.tran_user_id_u)));
        pairs.add(Pair.create(binding.itemUserMessage, context.getString(R.string.tran_message_u)));
        ActivityUtils.startActivityWithTransition(context, intent, pairs);
    }

    @MainThread
    @Override
    public void onLayoutLoaded() {
        //이미지가 로드된 후 트랜지션 개시
        Log.i("layout", "load finished");
        ActivityCompat.startPostponedEnterTransition(this);
    }
}
