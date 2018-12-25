package com.enos.totalsns.timelines;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.data.article.Article;
import com.enos.totalsns.util.autolink.AutoLinkMode;
import com.enos.totalsns.util.autolink.AutoLinkTextView;

import java.util.Date;
import java.util.List;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Article} and makes a call to the
 * specified {@link OnArticleClickListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private final List<Article> mValues;
    private final List<Article> mFilteredList;
    private final OnArticleClickListener mListener;

    public TimelineAdapter(List<Article> items, OnArticleClickListener listener) {
        mValues = items;
        mFilteredList = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Article article = mFilteredList.get(position);
        holder.mItem = article;
        holder.mIdView.setText(article.getUserId());
        holder.mContentView.addAutoLinkMode(
                AutoLinkMode.MODE_HASHTAG,
                AutoLinkMode.MODE_PHONE,
                AutoLinkMode.MODE_URL,
                AutoLinkMode.MODE_MENTION,
                AutoLinkMode.MODE_CUSTOM);
        //step1 required add auto link mode
        holder.mContentView.setCustomRegex("\\sutmostest\\b");
        //step1 optional add custom regex

        Context th = holder.mView.getContext();
        holder.mContentView.setHashtagModeColor(ContextCompat.getColor(th, R.color.red)); //setColor
        holder.mContentView.setPhoneModeColor(ContextCompat.getColor(th, R.color.text_yellow));
        holder.mContentView.setCustomModeColor(ContextCompat.getColor(th, R.color.green));
        holder.mContentView.setUrlModeColor(ContextCompat.getColor(th, R.color.blue));
        holder.mContentView.setMentionModeColor(ContextCompat.getColor(th, R.color.orange));
        holder.mContentView.setEmailModeColor(ContextCompat.getColor(th, R.color.gray));
        holder.mContentView.setSelectedStateColor(ContextCompat.getColor(th, R.color.batang_white)); //clickedColor
        holder.mContentView.setBoldAutoLinkModes(
                AutoLinkMode.MODE_HASHTAG,
                AutoLinkMode.MODE_PHONE,
                AutoLinkMode.MODE_URL,
                AutoLinkMode.MODE_EMAIL,
                AutoLinkMode.MODE_MENTION
        ); //bold
        holder.mContentView.enableUnderLine(); //underline
        // step2 optional set mode color, selected color, bold, underline

        holder.mContentView.setText(article.getMessage());
        //step3 required settext

        holder.mContentView.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> Toast.makeText(holder.mView.getContext(), autoLinkMode + " : " + matchedText, Toast.LENGTH_SHORT).show());
        //step4 required set on click listener

        Date date = new Date();
        date.setTime(article.getPostedAt());
        CharSequence dateStr = DateFormat.format("yyyy.M.d h:m a", date);
        holder.createDate.setText(article.getPostedAt() == 0 ? "" : dateStr);
        holder.mNameView.setText(article.getUserName());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onArticleClicked(holder.mItem, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIdView;
        final TextView mNameView;
        final TextView createDate;
        final AutoLinkTextView mContentView;
        final ImageView profileImage;
        Article mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.tlUserId);
            mNameView = view.findViewById(R.id.tlUserName);
            createDate = view.findViewById(R.id.tlTime);
            mContentView = view.findViewById(R.id.tlMessage);
            profileImage = view.findViewById(R.id.tlProfileImg);
        }

        @Override
        public String toString() {
            return super.toString() + " " + mItem.getMessage();
        }
    }
}
