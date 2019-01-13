package com.enos.totalsns.timeline.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.enos.totalsns.ContentsActivity;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.FragmentTimelineDetailBinding;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.ViewModelFactory;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ContentsActivity}
 * in two-pane mode (on tablets) or a {@link TimelineDetailActivity}
 * on handsets.
 */
public class TimelineDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ITEM_ARTICLE = "item_article";

    private Article mArticle;

    TimelineDetailViewModel viewModel;

    FragmentTimelineDetailBinding mDataBinding;
    private OnArticleClickListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TimelineDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ITEM_ARTICLE)) {
            mArticle = getArguments().getParcelable(ITEM_ARTICLE);
        }

        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getActivity())).get(TimelineDetailViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline_detail, container, false);

        // Show the dummy content as text in a TextView.
        updateUI();

        return mDataBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArticleClickListener) {
            mListener = (OnArticleClickListener) context;
        } else {
            throw new IllegalArgumentException("you must implement OnArticleClickListener");
        }
    }

    private void updateUI() {
        if (mArticle != null) {
            CollapsingToolbarLayout appBarLayout = getActivity().findViewById(R.id.toolbar_layout);
            if (appBarLayout != null && mArticle != null) {
                appBarLayout.setTitle(getString(R.string.title_timeline_detail));
            }
            GlideUtils.loadProfileImage(getContext(), mArticle.getProfileImg(), mDataBinding.tldProfileImg);
            mDataBinding.tldProfileImg.setOnClickListener((v) -> {
                if (mListener != null) mListener.onArticleProfileImgClicked(mArticle);
            });

            mDataBinding.tldUserId.setText(mArticle.getUserId());
            mDataBinding.tldTime.setText(ConvertUtils.getDateString(mArticle.getPostedAt()));
            mDataBinding.tldUserName.setText(mArticle.getUserName());

            String[] imgUrls = mArticle.getImageUrls();
            int urlSize = imgUrls.length;
            String firstImage = null;
            if (imgUrls != null && urlSize > 0) firstImage = imgUrls[0];
            boolean hasImage = firstImage != null && firstImage.length() > 0;
            mDataBinding.imageContainer.setVisibility(hasImage ? View.VISIBLE : View.GONE);
            mDataBinding.imageContainer.setImageCount(urlSize);
            mDataBinding.imageContainer.setOnImageClickedListener((iv, pos) -> {
                if (mListener != null) mListener.onArticleImageClicked(iv, mArticle, pos);
            });
            if (hasImage) {
                mDataBinding.imageContainer.loadImageViewsWithGlide(Glide.with(mDataBinding.imageContainer.getContext()), imgUrls);
            }

            ActivityUtils.setAutoLinkTextView(mDataBinding.getRoot().getContext(), mDataBinding.tldMessage, mArticle.getMessage(), ((autoLinkMode, matchedText) -> {
                if (mListener != null)
                    mListener.onAutoLinkClicked(autoLinkMode, matchedText, mArticle.getUrlMap());
            }));
        }
    }
}
