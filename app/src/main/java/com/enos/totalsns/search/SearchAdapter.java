package com.enos.totalsns.search;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.databinding.ItemSearchHeaderBinding;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.userlist.OnUserClickListener;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.widget.HFSupportAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link UserInfo} and makes a call to the
 * specified {@link OnUserClickListener }.
 * TODO: Replace the implementation with code for your data type.
 */
public class SearchAdapter extends HFSupportAdapter {

    private List<UserInfo> mFilteredList;
    private List<Article> mValues;

    private OnUserClickListener mListener;
    private OnArticleClickListener mArticleListener;
    private OnMoreUserButtonClickListener moreUserButtonClickListener;

    private final int TYPE_TWITTER = Constants.TWITTER;
    private final int TYPE_FACEBOOK = Constants.FACEBOOK;
    private final int TYPE_INSTAGRAM = Constants.INSTAGRAM;

    private int mSnsType = Constants.DEFAULT_SNS;

    private RecyclerView.RecycledViewPool recycledViewPool;

    private RecyclerView mUserRecyclerView;
    private boolean isItemChanged = false;

    private ItemSearchHeaderBinding headerViewHolder = null;

    public SearchAdapter(List<UserInfo> items, List<Article> list,
                         OnUserClickListener mListener, OnArticleClickListener articleClickListener,
                         OnMoreUserButtonClickListener moreUserButtonClickListener) {
        mValues = list;
        mFilteredList = items;
        this.mListener = mListener;
        this.mArticleListener = articleClickListener;
        this.moreUserButtonClickListener = moreUserButtonClickListener;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public List<?> getItems() {
        return mValues;
    }

    public List<UserInfo> getUserList() {
        return mFilteredList;
    }

    public List<Article> getArticleList() {
        return mValues;
    }

    @Override
    public boolean isItemChanged() {
        return isItemChanged;
    }

    @Override
    public void setIsItemChanged(boolean changed) {
        isItemChanged = changed;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSearchHeaderBinding itemHeaderBinding = ItemSearchHeaderBinding.inflate(inflater, parent, false);
        return new ArticleHeaderViewHolder(itemHeaderBinding);
    }

    @Override
    public FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSearchHeaderBinding itemFooterBinding = ItemSearchHeaderBinding.inflate(inflater, parent, false);
        return new ArticleFooterViewHolder(itemFooterBinding);
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemArticleBinding itemUserBinding = ItemArticleBinding.inflate(inflater, parent, false);
        return new ArticleViewHolder(itemUserBinding);
    }

    @Override
    public void onBindHeaderViewHolder(HFSupportAdapter.HeaderViewHolder vh, int pos) {
        ArticleHeaderViewHolder holder = (ArticleHeaderViewHolder) vh;
        holder.bind();
    }

    @Override
    public void onBindFooterViewHolder(HFSupportAdapter.FooterViewHolder vh, int pos) {
        ArticleFooterViewHolder holder = (ArticleFooterViewHolder) vh;
        holder.bind();
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder vh, int position) {
        if (mValues == null) return;
        ArticleViewHolder holder = (ArticleViewHolder) vh;
        holder.mItem = mValues.get(position);
        holder.bind(position);
    }

    @Override
    public int getYourItemViewType(int position) {
        return 0;
    }

    public void swapTimelineList(List<Article> list) {
        isItemChanged = true;
        if (list == null || mValues == null) {
            mValues = list;
            notifyDataSetChanged();
            return;
        }
        if (mValues == null) {
            mValues = list;
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {

                @Override
                public int getOldListSize() {
                    return mValues.size();
                }

                @Override
                public int getNewListSize() {
                    return list.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mValues.get(oldItemPosition).getTablePlusArticleId().equals(list.get(newItemPosition).getTablePlusArticleId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Article oldArticle = mValues.get(oldItemPosition);
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
            mValues = list;
            result.dispatchUpdatesTo(this);
        }
    }

    public void swapUserList(List<UserInfo> list) {
        mFilteredList = list;
        if (mUserRecyclerView != null) {
            headerViewHolder.getRoot().setVisibility(mFilteredList == null || mFilteredList.size() <= 0 ? View.GONE : View.VISIBLE);
            ((SearchedUserAdapter) mUserRecyclerView.getAdapter()).swapUserList(list);
        }
    }

    private class ArticleViewHolder extends ItemViewHolder {
        public final ItemArticleBinding binding;
        private Article mItem;

        ArticleViewHolder(ItemArticleBinding view) {
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
                if (mArticleListener != null)
                    mArticleListener.onArticleImageClicked(iv, mItem, pos);
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
                if (null != mArticleListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mArticleListener.onArticleClicked(binding, mItem, position);
                }
            });
            binding.tlProfileImg.setOnClickListener(v -> {
                if (null != mArticleListener)
                    mArticleListener.onArticleProfileImgClicked(mItem);
            });
        }
    }

    private class ArticleHeaderViewHolder extends HeaderViewHolder {
        public final ItemSearchHeaderBinding binding;

        ArticleHeaderViewHolder(ItemSearchHeaderBinding view) {
            super(view.getRoot());
            binding = view;
            headerViewHolder = view;
            headerViewHolder.getRoot().setVisibility(mFilteredList == null || mFilteredList.size() <= 0 ? View.GONE : View.VISIBLE);
        }

        public void bind() {
            mUserRecyclerView = binding.searchRv;
            SearchedUserAdapter userAdapter = null;
            if (mUserRecyclerView.getAdapter() != null && mUserRecyclerView.getAdapter() instanceof SearchedUserAdapter) {
                userAdapter = (SearchedUserAdapter) mUserRecyclerView.getAdapter();
//                userAdapter.swapUserList(mFilteredList);
            }
            if (userAdapter == null) {
                userAdapter = new SearchedUserAdapter(mFilteredList, mListener);
                binding.searchRv.setAdapter(userAdapter);
            }
            binding.searchRv.setHasFixedSize(true);
            binding.searchRv.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.searchRv.setRecycledViewPool(recycledViewPool);
            binding.searchMoreUser.setOnClickListener(v -> {
                if (moreUserButtonClickListener != null)
                    moreUserButtonClickListener.onMoreUserButtonClicked();
            });
        }
    }

    private class ArticleFooterViewHolder extends FooterViewHolder {
        public final ItemSearchHeaderBinding binding;

        ArticleFooterViewHolder(ItemSearchHeaderBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
        }
    }
}