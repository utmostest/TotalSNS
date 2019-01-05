package com.enos.totalsns.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.FragmentProfileBinding;
import com.enos.totalsns.follow.OnFollowListener;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.ViewModelFactory;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;

    public static final String ARG_USER_ID = "user-id";
    public static final long INVALID_ID = -1;
    private long mUserId = INVALID_ID;
    private FragmentProfileBinding dataBinding;
    private OnFollowListener mListener;

    public static ProfileFragment newInstance(long userId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_USER_ID, userId);
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(arguments);
        return profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getLong(ARG_USER_ID, INVALID_ID);
        }
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(ProfileViewModel.class);
        if (mUserId != INVALID_ID) mViewModel.fetchProfile(mUserId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initObserver();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFollowListener) {
            mListener = (OnFollowListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFollowListener");
        }
    }

    private void initView(UserInfo userInfo) {
        dataBinding.profileAddress.setText(userInfo.getLocation());
        dataBinding.profileCreated.setText(ConvertUtils.getDateString(userInfo.getCreatedAt()));
        dataBinding.profileFollowerNum.setText(String.valueOf(userInfo.getFollowerCount()));
        dataBinding.profileFollowingNum.setText(String.valueOf(userInfo.getFollowingCount()));
        dataBinding.profileFollowingNum.setOnClickListener(v -> {
            if (mListener != null) mListener.onFollowClicked(userInfo, false);
        });
        dataBinding.profileFollowingLabel.setOnClickListener(v -> {
            if (mListener != null) mListener.onFollowClicked(userInfo, false);
        });
        dataBinding.profileFollowerNum.setOnClickListener(v -> {
            if (mListener != null) mListener.onFollowClicked(userInfo, true);
        });
        dataBinding.profileFollowerLabel.setOnClickListener(v -> {
            if (mListener != null) mListener.onFollowClicked(userInfo, true);
        });
        if (userInfo.getLastArticle() != null) {
            if (ConvertUtils.getActualSize(userInfo.getLastArticle().getImageUrls()) > 0) {
                dataBinding.profileArticle.imageContainer.setVisibility(View.VISIBLE);
                dataBinding.profileArticle.imageContainer.setImageCount(ConvertUtils.getActualSize(userInfo.getLastArticle().getImageUrls()));
                dataBinding.profileArticle.imageContainer.loadImageViewsWithGlide(Glide.with(getContext()), userInfo.getLastArticle().getImageUrls());
            } else {
                dataBinding.profileArticle.imageContainer.setVisibility(View.GONE);
            }
            dataBinding.profileArticle.getRoot().setVisibility(View.VISIBLE);
            dataBinding.profileArticle.tlUserName.setText(userInfo.getLastArticle().getUserName());
            dataBinding.profileArticle.tlUserId.setText(userInfo.getLastArticle().getUserId());
            dataBinding.profileArticle.tlTime.setText(ConvertUtils.getDateString(userInfo.getLastArticle().getPostedAt()));
            ActivityUtils.setAutoLinkTextView(getContext(), dataBinding.profileArticle.tlMessage, userInfo.getLastArticle().getMessage(), userInfo.getLastArticle().getUrlMap());

            GlideUtils.loadProfileImage(getContext(), userInfo.getLastArticle().getProfileImg(), dataBinding.profileArticle.tlProfileImg);
        } else {
            dataBinding.profileArticle.getRoot().setVisibility(View.GONE);
        }
        dataBinding.itemUserMessage.setText(userInfo.getMessage());
        dataBinding.itemUserName.setText(userInfo.getUserName());
        dataBinding.itemUserScreenId.setText(userInfo.getUserId());

        GlideUtils.loadProfileImage(getContext(), userInfo.getProfileImg(), dataBinding.itemUserProfile);

        if (ConvertUtils.isStringValid(userInfo.getProfileBackImg())) {
            GlideUtils.loadBackImage(getContext(), userInfo.getProfileBackImg(), dataBinding.itemUserProfileBack);
        } else if (ConvertUtils.isStringValid(userInfo.getProfileBackColor())) {
            SingletonToast.getInstance().log("backcolor", userInfo.getProfileBackColor());
            dataBinding.itemUserProfileBack.setImageDrawable(null);
            dataBinding.itemUserProfileBack.setBackgroundColor(Color.parseColor("#" + userInfo.getProfileBackColor()));
        } else {
            dataBinding.itemUserProfileBack.setImageResource(R.drawable.side_nav_bar);
        }
    }

    private void initObserver() {
        mViewModel.getUserProfile().observe(this, (user) -> {
            if (user != null) {
                initView(user);
            }
        });
    }
}
