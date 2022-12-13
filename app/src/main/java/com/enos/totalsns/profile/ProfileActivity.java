package com.enos.totalsns.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.enos.totalsns.ContentsActivity;
import com.enos.totalsns.R;
import com.enos.totalsns.custom.autolink.AutoLinkMode;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.image.ImageActivity;
import com.enos.totalsns.listener.OnArticleClickListener;
import com.enos.totalsns.listener.OnFollowListener;
import com.enos.totalsns.listener.OnLoadLayoutListener;
import com.enos.totalsns.timeline.detail.TimelineDetailActivity;
import com.enos.totalsns.userlist.UserListActivity;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.StringUtils;
import com.enos.totalsns.util.ViewModelFactory;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements OnFollowListener, OnArticleClickListener, OnLoadLayoutListener {

    private long userId = ProfileFragment.INVALID_ID;
    private UserInfo userInfo;

//    private ActivityProfileBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mBinding = ActivityProfileBinding.inflate(getLayoutInflater());
//        setContentView(mBinding.getRoot());
        DataBindingUtil.setContentView(this, R.layout.activity_profile);
        initActionBar();
        if (savedInstanceState == null) {
            initFragment();
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initFragment() {
        userInfo = getIntent().getParcelableExtra(ProfileFragment.ARG_USER_INFO);
        userId = getIntent().getLongExtra(ProfileFragment.ARG_USER_ID, ProfileFragment.INVALID_ID);
        if (userInfo != null) {
            Log.i("layout", "load started");
            //이미지가 로드될때까지 트랜지션 연기
//            ActivityCompat.postponeEnterTransition(this);
            getSupportFragmentManager().beginTransaction().add(R.id.container, ProfileFragment.newInstance(userInfo)).commitNow();
            setTitle(getString(R.string.title_profile, userInfo.getUserId()));
        } else if (userId > ProfileFragment.INVALID_ID) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, ProfileFragment.newInstance(userId)).commitNow();
            ProfileViewModel viewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(this)).get(ProfileViewModel.class);
            userInfo = viewModel.getuserFromCache(userId);
            setTitle(getString(R.string.title_profile, userInfo == null ? "USER" : userInfo.getUserId()));
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
    public void onFollowTextClicked(UserInfo info, boolean isFollower) {
        if (info.isProtected()) {
            Toast.makeText(this, "This user is protected", Toast.LENGTH_SHORT).show();
        } else {
            UserListActivity.startFollowList(this, info.getLongUserId(), isFollower);
        }
    }

    @Override
    public void onArticleClicked(ItemArticleBinding binding, Article mItem) {
//        TimelineDetailActivity.startWithTransition(this, binding, mItem, StringUtils.getActualSize(mItem.getImageUrls()) > 0);
        TimelineDetailActivity.start(this, mItem);
    }

    @Override
    public void onArticleImageClicked(ImageView iv, Article article, int position) {
        ImageActivity.start(this, article.getImageUrls(), position);
    }

    @Override
    public void onArticleProfileImgClicked(Article article) {
        ImageActivity.start(this, new String[]{article.getProfileImg()}, 0);
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
                break;
            case MODE_PHONE:
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + matchedText));
                ActivityUtils.checkResolveAndStartActivity(intent, this);
                break;
            case MODE_EMAIL:
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{matchedText});
                ActivityUtils.checkResolveAndStartActivity(intent, this);
                break;
            case MODE_HASHTAG:
            case MODE_MENTION:
                ContentsActivity.startWithQuery(this, matchedText);
                finish();
                break;
        }
    }

    @MainThread
    @Override
    public void onLayoutLoaded() {
        //이미지가 로드된 후 트랜지션 개시
        Log.i("layout", "load finished");
        if (userInfo == null) {
            ProfileViewModel viewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(this)).get(ProfileViewModel.class);
            UserInfo current = viewModel.getuserFromCache(userId);
            setTitle(getString(R.string.title_profile, current == null ? "USER" : current.getUserId()));
        } else {
//            ActivityCompat.startPostponedEnterTransition(this);
        }
    }

    public static void start(AppCompatActivity context, UserInfo userInfo) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileFragment.ARG_USER_INFO, userInfo);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }

    public static void start(AppCompatActivity context, long longUserId) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileFragment.ARG_USER_ID, longUserId);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }

//    public static void startWithTransition(AppCompatActivity context, ItemUserBinding binding, UserInfo mItem) {
//        Intent intent = new Intent(context, ProfileActivity.class);
//        intent.putExtra(ProfileFragment.ARG_USER_INFO, mItem);
//        ArrayList<Pair<View, String>> pairs = new ArrayList<>();
//        pairs.add(Pair.create(binding.fProfileImg, context.getString(R.string.tran_profile_image_u)));
//        pairs.add(Pair.create(binding.fFollowBtn, context.getString(R.string.tran_follow_btn)));
//        pairs.add(Pair.create(binding.fUserName, context.getString(R.string.tran_user_name_u)));
//        pairs.add(Pair.create(binding.fUserId, context.getString(R.string.tran_user_id_u)));
//        pairs.add(Pair.create(binding.fMessage, context.getString(R.string.tran_message_u)));
//        ActivityUtils.startActivityWithTransition(context, intent, pairs);
//    }
//
//    public static void startWithTransition(AppCompatActivity context, ItemSearchUserBinding binding, UserInfo mItem) {
//        Intent intent = new Intent(context, ProfileActivity.class);
//        intent.putExtra(ProfileFragment.ARG_USER_INFO, mItem);
//        ArrayList<Pair<View, String>> pairs = new ArrayList<>();
//        pairs.add(Pair.create(binding.itemUserProfile, context.getString(R.string.tran_profile_image_u)));
//        pairs.add(Pair.create(binding.itemUserProfileBack, context.getString(R.string.tran_profile_back)));
//        pairs.add(Pair.create(binding.itemUserFollowBtn, context.getString(R.string.tran_follow_btn)));
//        pairs.add(Pair.create(binding.itemUserName, context.getString(R.string.tran_user_name_u)));
//        pairs.add(Pair.create(binding.itemUserScreenId, context.getString(R.string.tran_user_id_u)));
//        pairs.add(Pair.create(binding.itemUserMessage, context.getString(R.string.tran_message_u)));
//        ActivityUtils.startActivityWithTransition(context, intent, pairs);
//    }
}
