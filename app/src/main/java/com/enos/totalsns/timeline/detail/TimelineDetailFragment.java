package com.enos.totalsns.timeline.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.enos.totalsns.LayoutLoad;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.FragmentTimelineDetailBinding;
import com.enos.totalsns.listener.OnArticleClickListener;
import com.enos.totalsns.listener.OnLoadLayoutListener;
import com.enos.totalsns.util.AutoLinkTextUtils;
import com.enos.totalsns.util.BitmapUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.TimeUtils;
import com.enos.totalsns.util.ViewModelFactory;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class TimelineDetailFragment extends Fragment {

    public static final String ITEM_ARTICLE = "item_article";

    private Article mArticle;

    TimelineDetailViewModel viewModel;

    FragmentTimelineDetailBinding mBinding;
    private OnArticleClickListener mListener;

    private LayoutLoad layoutLoad;

    public static TimelineDetailFragment newInstance(Article article) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ITEM_ARTICLE, article);
        TimelineDetailFragment fragment = new TimelineDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public TimelineDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ITEM_ARTICLE)) {
            mArticle = getArguments().getParcelable(ITEM_ARTICLE);
        }

        viewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(getActivity())).get(TimelineDetailViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline_detail, container, false);

        // Show the dummy content as text in a TextView.
        updateUI();

        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArticleClickListener && context instanceof OnLoadLayoutListener) {
            mListener = (OnArticleClickListener) context;
            layoutLoad = new LayoutLoad(true, false, (OnLoadLayoutListener) context);
        } else {
            throw new IllegalArgumentException("you must implement OnArticleClickListener and OnLoadLayoutListener");
        }
    }

    private void setLayoutLoaded() {
        if (layoutLoad != null) {
            layoutLoad.setImgLoadAndCallbackIfLaoded();
        }
    }

    private void updateUI() {
        if (mArticle != null) {
            CollapsingToolbarLayout appBarLayout = getActivity().findViewById(R.id.toolbar_layout);
            if (appBarLayout != null && mArticle != null) {
                appBarLayout.setTitle(getString(R.string.title_timeline_detail));
            }
            GlideUtils.loadProfileImage(getContext(), mArticle.getProfileImg(), new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    mBinding.tldProfileImg.setImageDrawable(resource);
                    setLayoutLoaded();
                }
            });
            mBinding.tldProfileImg.setOnClickListener((v) -> {
                if (mListener != null) mListener.onArticleProfileImgClicked(mArticle);
            });

            mBinding.tldUserId.setText(mArticle.getUserId());
            mBinding.tldTime.setText(TimeUtils.getDateString(mArticle.getPostedAt()));
            mBinding.tldUserName.setText(mArticle.getUserName());

            String[] imgUrls = mArticle.getImageUrls();
            int urlSize = imgUrls.length;
            String firstImage = null;
            if (imgUrls != null && urlSize > 0) firstImage = imgUrls[0];
            boolean hasImage = firstImage != null && firstImage.length() > 0;
            mBinding.imageContainer.setVisibility(hasImage ? View.VISIBLE : View.GONE);
            if (hasImage) {
                for (int i = 0; i < imgUrls.length; i++) {
                    final ImageView imageView = new ImageView(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.topMargin = getContext().getResources().getDimensionPixelSize(R.dimen.iv_margin);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    mBinding.imageContainer.addView(imageView);
                    GlideUtils.loadBigImage(getContext(), imgUrls[i], imageView, new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (resource != null) {
                                final int imageHeight = resource.getHeight();
                                final int imageWidth = resource.getWidth();

                                int width = imageView.getMeasuredWidth();
                                int height = (width * imageHeight) / imageWidth;

                                Bitmap resizedBmp = Bitmap.createScaledBitmap(resource, width, height, true);

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                                layoutParams.topMargin = getContext().getResources().getDimensionPixelSize(R.dimen.iv_margin);

                                imageView.setLayoutParams(layoutParams);
                                imageView.setImageBitmap(resizedBmp);
                            }
                            return true;
                        }
                    });
                }
            }
//            mBinding.imageContainer.setImageCount(urlSize);
//            mBinding.imageContainer.setOnImageClickedListener((iv, pos) -> {
//                if (mListener != null) mListener.onArticleImageClicked(iv, mArticle, pos);
//            });
//            if (hasImage) {
//                mBinding.imageContainer.loadImageViewsWithGlide(Glide.with(mBinding.imageContainer.getContext()), imgUrls);
//            }

            AutoLinkTextUtils.set(mBinding.getRoot().getContext(), mBinding.tldMessage, mArticle.getMessage(), ((autoLinkMode, matchedText) -> {
                if (mListener != null)
                    mListener.onAutoLinkClicked(autoLinkMode, matchedText, mArticle.getUrlMap());
            }));
        }
    }
}
