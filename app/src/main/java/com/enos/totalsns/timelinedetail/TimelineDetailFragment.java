package com.enos.totalsns.timelinedetail;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.FragmentTimelineDetailBinding;
import com.enos.totalsns.timelines.TimelineActivity;
import com.enos.totalsns.util.ViewModelFactory;
import com.enos.totalsns.util.autolink.AutoLinkMode;

import java.util.Date;

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
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            mArticle = getArguments().getParcelable(ITEM_ARTICLE);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null && mArticle != null) {
                appBarLayout.setTitle(mArticle.getUserName());
            }
            final long articleId = mArticle.getArticleId();
//            viewModel.getArticle(articleId).observe(this, article -> {
//                mArticle = article;
//            });
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

            mDataBinding.tldUserId.setText(mArticle.getUserId());
            Date date = new Date();
            date.setTime(mArticle.getPostedAt());
            CharSequence dateStr = DateFormat.format("yyyy.M.d h:m a", date);

            mDataBinding.tldTime.setText(mArticle.getPostedAt() == 0 ? "" : dateStr);
            mDataBinding.tldUserName.setText(mArticle.getUserName());

            mDataBinding.tldMessage.addAutoLinkMode(
                    AutoLinkMode.MODE_HASHTAG,
                    AutoLinkMode.MODE_PHONE,
                    AutoLinkMode.MODE_URL,
                    AutoLinkMode.MODE_MENTION,
                    AutoLinkMode.MODE_CUSTOM);
            //step1 required add auto link mode
            mDataBinding.tldMessage.setCustomRegex("\\sutmostest\\b");
            //step1 optional add custom regex

            Context th = this.getContext();
            mDataBinding.tldMessage.setHashtagModeColor(ContextCompat.getColor(th, R.color.red)); //setColor
            mDataBinding.tldMessage.setPhoneModeColor(ContextCompat.getColor(th, R.color.text_yellow));
            mDataBinding.tldMessage.setCustomModeColor(ContextCompat.getColor(th, R.color.green));
            mDataBinding.tldMessage.setUrlModeColor(ContextCompat.getColor(th, R.color.blue));
            mDataBinding.tldMessage.setMentionModeColor(ContextCompat.getColor(th, R.color.orange));
            mDataBinding.tldMessage.setEmailModeColor(ContextCompat.getColor(th, R.color.gray));
            mDataBinding.tldMessage.setSelectedStateColor(ContextCompat.getColor(th, R.color.batang_white)); //clickedColor
            mDataBinding.tldMessage.setBoldAutoLinkModes(
                    AutoLinkMode.MODE_HASHTAG,
                    AutoLinkMode.MODE_PHONE,
                    AutoLinkMode.MODE_URL,
                    AutoLinkMode.MODE_EMAIL,
                    AutoLinkMode.MODE_MENTION
            ); //bold
            mDataBinding.tldMessage.enableUnderLine(); //underline
            // step2 optional set mode color, selected color, bold, underline

            mDataBinding.tldMessage.setText(mArticle.getMessage());
            //step3 required settext

            mDataBinding.tldMessage.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> Toast.makeText(this.getContext(), autoLinkMode + " : " + matchedText, Toast.LENGTH_SHORT).show());
            //step4 required set on click listener
        }
    }
}
