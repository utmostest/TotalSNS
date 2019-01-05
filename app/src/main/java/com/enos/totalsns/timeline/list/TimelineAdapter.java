package com.enos.totalsns.timeline.list;


import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.GlideUtils;

import java.util.Arrays;
import java.util.List;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Article} and makes a call to the
 * specified {@link OnArticleClickListener}.
 * TODO: Replace the implementation with code for your data type.
 */
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
        holder.mItem = mFilteredList.get(position);
        holder.bind(position);
    }

    public void swapTimelineList(List<Article> list) {
        if (list == null) {
            mFilteredList = null;
            notifyDataSetChanged();
            return;
        }
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
                    return mFilteredList.get(oldItemPosition).getTablePlusArticleId().equals(list.get(newItemPosition).getTablePlusArticleId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Article oldArticle = mFilteredList.get(oldItemPosition);
                    Article newArticle = list.get(newItemPosition);
                    return oldArticle.getTablePlusArticleId().equals(newArticle.getTablePlusArticleId()) &&
                            oldArticle.getProfileImg().equals(newArticle.getProfileImg()) &&
                            oldArticle.getUserName().equals(newArticle.getUserName()) &&
                            oldArticle.getUserId().equals(newArticle.getUserId()) &&
                            oldArticle.getMessage().equals(newArticle.getMessage()) &&
                            oldArticle.getTableUserId() == newArticle.getTableUserId() &&
                            oldArticle.getArticleId() == newArticle.getArticleId() &&
                            oldArticle.getSnsType() == newArticle.getSnsType() &&
                            oldArticle.getPostedAt() == newArticle.getPostedAt() &&
                            Arrays.equals(oldArticle.getImageUrls(), newArticle.getImageUrls()) &&
                            ConvertUtils.equalsHashMap(oldArticle.getUrlMap(), newArticle.getUrlMap());

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
        private Article mItem;

        ItemViewHolder(ItemArticleBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind(int position) {
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getProfileImg(), binding.tlProfileImg);

            final String[] imgUrls = mItem.getImageUrls();
            int urlSize = ConvertUtils.getActualSize(imgUrls);
            boolean hasImage = urlSize > 0;
            binding.imageContainer.setVisibility(hasImage ? View.VISIBLE : View.GONE);
            binding.imageContainer.setImageCount(urlSize);
            //Log.i("bind", "urlSize : " + urlSize + ", imgUrls : " + Arrays.toString(imgUrls));
            binding.imageContainer.setOnImageClickedListener((iv, pos) -> {
                if (mListener != null) mListener.onArticleImageClicked(iv, mItem, pos);
            });
            if (hasImage) {
                binding.imageContainer.loadImageViewsWithGlide(Glide.with(binding.imageContainer.getContext()), imgUrls);
            }

            binding.tlUserId.setText(mItem.getUserId());

            ActivityUtils.setAutoLinkTextView(binding.getRoot().getContext(), binding.tlMessage, mItem.getMessage(), mItem.getUrlMap());

            binding.tlTime.setText(ConvertUtils.getDateString(mItem.getPostedAt()));
            binding.tlUserName.setText(mItem.getUserName());
            if (mItem.getUrlMap() != null) {
//                Log.i("url", article.getUrlMap().keySet() + "\n" + article.getUrlMap().values());
                for (String key : mItem.getUrlMap().keySet()) {
//                    Log.i("url", key + "\n" + article.getUrlMap().get(key));
                }
            }

            binding.getRoot().setOnClickListener(v -> {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onArticleClicked(binding,mItem, position);
                }
            });
            binding.tlProfileImg.setOnClickListener(v -> {
                if (null != mListener)
                    mListener.onArticleImageClicked((ImageView) v, mItem, position);
            });
        }
    }
}
