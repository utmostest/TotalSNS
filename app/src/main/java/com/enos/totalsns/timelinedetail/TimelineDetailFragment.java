package com.enos.totalsns.timelinedetail;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.FragmentTimelineDetailBinding;
import com.enos.totalsns.timelines.TimelineActivity;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ConverUtils;
import com.enos.totalsns.util.ViewModelFactory;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link TimelineActivity}
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

    private void updateUI() {
        if (mArticle != null) {
            CollapsingToolbarLayout appBarLayout = getActivity().findViewById(R.id.toolbar_layout);
            if (appBarLayout != null && mArticle != null) {
                appBarLayout.setTitle(getString(R.string.title_timeline_detail));
            }

            Glide.with(getContext())
                    .load(mArticle.getProfileImg())
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.drawable.ic_account_circle_black_48dp)
                                    .dontTransform()
                                    .optionalCircleCrop()
                    )
                    .transition(
                            new DrawableTransitionOptions()
                                    .crossFade(100)
                    )
                    .into(mDataBinding.tldProfileImg);

            mDataBinding.tldUserId.setText(mArticle.getUserId());
            mDataBinding.tldTime.setText(ConverUtils.getDateString(mArticle.getPostedAt()));
            mDataBinding.tldUserName.setText(mArticle.getUserName());

            ActivityUtils.setAutoLinkTextView(mDataBinding.getRoot().getContext(), mDataBinding.tldMessage, mArticle);
        }
    }
}
