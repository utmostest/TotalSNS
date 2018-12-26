package com.enos.totalsns.timelinedetail;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.util.autolink.AutoLinkMode;
import com.enos.totalsns.util.autolink.AutoLinkTextView;
import com.enos.totalsns.timelines.TimelineActivity;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mArticle != null) {
            TextView userId = rootView.findViewById(R.id.tldUserId);
            userId.setText(mArticle.getUserId());
            Date date = new Date();
            date.setTime(mArticle.getPostedAt());
            CharSequence dateStr = DateFormat.format("yyyy.M.d h:m a", date);
            TextView time = rootView.findViewById(R.id.tldTime);
            time.setText(mArticle.getPostedAt() == 0 ? "" : dateStr);
            TextView name = rootView.findViewById(R.id.tldUserName);
            name.setText(mArticle.getUserName());

            AutoLinkTextView autoLinkTextView = rootView.findViewById(R.id.tldMessage);
            autoLinkTextView.addAutoLinkMode(
                    AutoLinkMode.MODE_HASHTAG,
                    AutoLinkMode.MODE_PHONE,
                    AutoLinkMode.MODE_URL,
                    AutoLinkMode.MODE_MENTION,
                    AutoLinkMode.MODE_CUSTOM);
            //step1 required add auto link mode
            autoLinkTextView.setCustomRegex("\\sutmostest\\b");
            //step1 optional add custom regex

            Context th = this.getContext();
            autoLinkTextView.setHashtagModeColor(ContextCompat.getColor(th, R.color.red)); //setColor
            autoLinkTextView.setPhoneModeColor(ContextCompat.getColor(th, R.color.text_yellow));
            autoLinkTextView.setCustomModeColor(ContextCompat.getColor(th, R.color.green));
            autoLinkTextView.setUrlModeColor(ContextCompat.getColor(th, R.color.blue));
            autoLinkTextView.setMentionModeColor(ContextCompat.getColor(th, R.color.orange));
            autoLinkTextView.setEmailModeColor(ContextCompat.getColor(th, R.color.gray));
            autoLinkTextView.setSelectedStateColor(ContextCompat.getColor(th, R.color.batang_white)); //clickedColor
            autoLinkTextView.setBoldAutoLinkModes(
                    AutoLinkMode.MODE_HASHTAG,
                    AutoLinkMode.MODE_PHONE,
                    AutoLinkMode.MODE_URL,
                    AutoLinkMode.MODE_EMAIL,
                    AutoLinkMode.MODE_MENTION
            ); //bold
            autoLinkTextView.enableUnderLine(); //underline
            // step2 optional set mode color, selected color, bold, underline

            autoLinkTextView.setText(mArticle.getMessage());
            //step3 required settext

            autoLinkTextView.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> Toast.makeText(this.getContext(), autoLinkMode + " : " + matchedText, Toast.LENGTH_SHORT).show());
            //step4 required set on click listener
        }

        return rootView;
    }
}
