package com.enos.totalsns.timelines;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.util.autolink.AutoLinkMode;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Article} and makes a call to the
 * specified {@link OnArticleClickListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Article> mValues;
    private List<Article> mFilteredList;
    private final OnArticleClickListener mListener;

    public TimelineAdapter(List<Article> items, OnArticleClickListener listener) {
        mValues = items;
        mFilteredList = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemArticleBinding itemAccountBinding = ItemArticleBinding.inflate(inflater, parent, false);
        return new ItemViewHolder(itemAccountBinding);
    }

    public void swapTimelineList(List<Article> list) {
        if (mFilteredList == null) {
            mFilteredList = list;
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {

                @Override
                public int getOldListSize() {
                    return mFilteredList.size();
                }

                @Override
                public int getNewListSize() {
                    return list.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mFilteredList.get(oldItemPosition).getArticleId() == list.get(newItemPosition).getArticleId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Article oldArticle = mFilteredList.get(oldItemPosition);
                    Article newArticle = list.get(newItemPosition);
                    return oldArticle.getArticleId() == newArticle.getArticleId() &&
                            oldArticle.getSnsType() == newArticle.getSnsType() &&
                            oldArticle.getPostedAt() == newArticle.getPostedAt() &&
                            oldArticle.getProfileImg().equals(newArticle.getProfileImg()) &&
                            oldArticle.getUserName().equals(newArticle.getUserName()) &&
                            oldArticle.getUserId().equals(newArticle.getUserId()) &&
                            Arrays.equals(oldArticle.getImageUrls(), newArticle.getImageUrls()) &&
                            oldArticle.getMessage().equals(newArticle.getMessage());
                }
            },true);
            mFilteredList = list;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder vh, final int position) {
        if (mFilteredList == null) return;
        ItemViewHolder holder = (ItemViewHolder) vh;
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mFilteredList == null) return 0;
        return mFilteredList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        public final ItemArticleBinding binding;
        Article mItem;

        ItemViewHolder(ItemArticleBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind(final int position) {
            if (mFilteredList == null || mFilteredList.size() <= position) return;
            Article article = mFilteredList.get(position);
            mItem = article;
            binding.tlUserId.setText(article.getUserId());
            binding.tlMessage.addAutoLinkMode(
                    AutoLinkMode.MODE_HASHTAG,
                    AutoLinkMode.MODE_PHONE,
                    AutoLinkMode.MODE_URL,
                    AutoLinkMode.MODE_MENTION,
                    AutoLinkMode.MODE_CUSTOM);
            //step1 required add auto link mode
            binding.tlMessage.setCustomRegex("\\sutmostest\\b");
            //step1 optional add custom regex

            Context th = binding.getRoot().getContext();
            binding.tlMessage.setHashtagModeColor(ContextCompat.getColor(th, R.color.red)); //setColor
            binding.tlMessage.setPhoneModeColor(ContextCompat.getColor(th, R.color.text_yellow));
            binding.tlMessage.setCustomModeColor(ContextCompat.getColor(th, R.color.green));
            binding.tlMessage.setUrlModeColor(ContextCompat.getColor(th, R.color.blue));
            binding.tlMessage.setMentionModeColor(ContextCompat.getColor(th, R.color.orange));
            binding.tlMessage.setEmailModeColor(ContextCompat.getColor(th, R.color.gray));
            binding.tlMessage.setSelectedStateColor(ContextCompat.getColor(th, R.color.batang_white)); //clickedColor
            binding.tlMessage.setBoldAutoLinkModes(
                    AutoLinkMode.MODE_HASHTAG,
                    AutoLinkMode.MODE_PHONE,
                    AutoLinkMode.MODE_URL,
                    AutoLinkMode.MODE_EMAIL,
                    AutoLinkMode.MODE_MENTION
            ); //bold
            binding.tlMessage.enableUnderLine(); //underline
            // step2 optional set mode color, selected color, bold, underline

            binding.tlMessage.setText(article.getMessage());
            //step3 required settext

            binding.tlMessage.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> Toast.makeText(th, autoLinkMode + " : " + matchedText, Toast.LENGTH_SHORT).show());
            //step4 required set on click listener

            Date date = new Date();
            date.setTime(article.getPostedAt());
            CharSequence dateStr = DateFormat.format("yyyy.M.d h:m a", date);
            binding.tlTime.setText(article.getPostedAt() == 0 ? "" : dateStr);
            binding.tlUserName.setText(article.getUserName());

            binding.getRoot().setOnClickListener(v -> {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onArticleClicked(mItem, position);
                }
            });
        }
    }
}
