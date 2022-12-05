package com.enos.totalsns.timeline.list;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.listener.OnArticleClickListener;
import com.enos.totalsns.util.AutoLinkTextUtils;
import com.enos.totalsns.util.CompareUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.StringUtils;
import com.enos.totalsns.util.TimeUtils;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Article> mFilteredList;
    private final OnArticleClickListener mListener;

    public TimelineAdapter(List<Article> items, OnArticleClickListener listener) {
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

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder vh, final int position) {
        if (mFilteredList == null) return;
        ItemViewHolder holder = (ItemViewHolder) vh;
        holder.bind(mFilteredList.get(position));
    }

    public void swapTimelineList(List<Article> list) {
        if (list == null || mFilteredList == null) {
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
                    return CompareUtils.isArticleSame(mFilteredList.get(oldItemPosition), list.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return CompareUtils.isArticleEqual(mFilteredList.get(oldItemPosition), list.get(newItemPosition));
                }
            }, true);
            mFilteredList = list;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public int getItemCount() {
        if (mFilteredList == null) return 0;
        return mFilteredList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        public final ItemArticleBinding binding;

        ItemViewHolder(ItemArticleBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind(Article mItem) {
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getProfileImg(), binding.tlProfileImg);

            final String[] imgUrls = mItem.getImageUrls();
            int urlSize = StringUtils.getActualSize(imgUrls);
            boolean hasImage = urlSize > 0;
            binding.imageContainer.setVisibility(hasImage ? View.VISIBLE : View.GONE);
            binding.imageContainer.setImageCount(urlSize);
            binding.imageContainer.setOnImageClickedListener((iv, pos) -> {
                if (mListener != null) mListener.onArticleImageClicked(iv, mItem, pos);
            });
            if (hasImage) {
                binding.imageContainer.loadImageViewsWithGlide(Glide.with(binding.imageContainer.getContext()), imgUrls);
            }

            binding.tlUserId.setText(mItem.getUserId());

            AutoLinkTextUtils.set(binding.getRoot().getContext(), binding.tlMessage, mItem.getMessage(), (autoLinkMode, autoLinkText) -> {
                if (mListener != null)
                    mListener.onAutoLinkClicked(autoLinkMode, autoLinkText, mItem.getUrlMap());
            });

            binding.tlTime.setText(TimeUtils.getDateString(mItem.getPostedAt()));
            binding.tlUserName.setText(mItem.getUserName());

            binding.getRoot().setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onArticleClicked(binding, mItem);
                }
            });
            binding.tlProfileImg.setOnClickListener(v -> {
                if (null != mListener)
                    mListener.onArticleProfileImgClicked(mItem);
            });
        }
    }
}
