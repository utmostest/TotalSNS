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
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.GlideUtils;

import java.util.Arrays;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link UserInfo} and makes a call to the
 * specified {@link OnUserClickListener }.
 * TODO: Replace the implementation with code for your data type.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserInfo> mFilteredList;
    private List<Article> mValues;

    private OnUserClickListener mListener;
    private OnArticleClickListener mArticleListener;

    private final int TYPE_HEADER = -2;
    private final int TYPE_FOOTER = -1;
    private final int TYPE_TWITTER = Constants.TWITTER;
    private final int TYPE_FACEBOOK = Constants.FACEBOOK;
    private final int TYPE_INSTAGRAM = Constants.INSTAGRAM;

    private boolean mIsEnableFooter = false;
    private boolean mIsEnableHeader = false;

    private boolean mIsEnableFooterAlways = false;
    private boolean mIsEnableHeaderAlways = false;

    private int mSnsType = Constants.DEFAULT_SNS;

    private RecyclerView.RecycledViewPool recycledViewPool;

    private RecyclerView mUserRecyclerView;

    public SearchAdapter(List<UserInfo> items, List<Article> list, OnUserClickListener mListener, OnArticleClickListener articleClickListener) {
        mValues = list;
        mFilteredList = items;
        this.mListener = mListener;
        this.mArticleListener = articleClickListener;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            ItemSearchHeaderBinding itemHeaderBinding = ItemSearchHeaderBinding.inflate(inflater, parent, false);
            return new HeaderViewHolder(itemHeaderBinding);
        } else if (viewType == TYPE_FOOTER) {
            ItemSearchHeaderBinding itemFooterBinding = ItemSearchHeaderBinding.inflate(inflater, parent, false);
            return new FooterViewHolder(itemFooterBinding);
        } else {
            ItemArticleBinding itemUserBinding = ItemArticleBinding.inflate(inflater, parent, false);
            return new ArticleViewHolder(itemUserBinding);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int pos) {

        if (vh instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) vh;
            holder.bind();
        } else if (vh instanceof FooterViewHolder) {
            FooterViewHolder holder = (FooterViewHolder) vh;
            holder.bind();
        } else {
            int position = getActualPosition(pos);
            if (position < 0) return;

            if (mValues == null) return;
            ArticleViewHolder holder = (ArticleViewHolder) vh;
            holder.mItem = mValues.get(position);
            holder.bind(position);
        }
    }

    private int getActualPosition(int pos) {
        int size = mValues == null ? 0 : mValues.size();
        boolean isHeaderEnabled = (mIsEnableHeader && mIsEnableHeaderAlways) || (mIsEnableHeader && size > 0);

        if (size <= 0) return -1;
        int position = (pos - (isHeaderEnabled ? 1 : 0));
        return position;
    }

    @Override
    public int getItemCount() {
        int size = mValues == null ? 0 : mValues.size();
        boolean isHeaderEnabled = (mIsEnableHeader && mIsEnableHeaderAlways) || (mIsEnableHeader && size > 0);
        boolean isFooterEnabled = (mIsEnableFooter && mIsEnableFooterAlways) || (mIsEnableFooter && size > 0);

        return size + (isHeaderEnabled ? 1 : 0) + (isFooterEnabled ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        int headerPos = -1;
        int footerPos = -1;
        int size = mValues == null ? 0 : mValues.size();
        boolean isHeaderEnabled = (mIsEnableHeader && mIsEnableHeaderAlways) || (mIsEnableHeader && size > 0);
        boolean isFooterEnabled = (mIsEnableFooter && mIsEnableFooterAlways) || (mIsEnableFooter && size > 0);

        int currentType = mSnsType;

        if (isHeaderEnabled) {
            headerPos = 0;
        }
        if (isFooterEnabled) {
            footerPos = size + headerPos + 1;
        }

        if (position == headerPos) {
            return TYPE_HEADER;
        } else if (position == footerPos) {
            return TYPE_FOOTER;
        }
        return currentType;
    }

    public void swapTimelineList(List<Article> list) {
        if (list == null) {
            mValues = null;
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
        if (mUserRecyclerView != null) {
            ((UserAdapter) mUserRecyclerView.getAdapter()).swapUserList(list);
        }
    }

    private class ArticleViewHolder extends RecyclerView.ViewHolder {
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
                if (mArticleListener != null) mArticleListener.onArticleImageClicked(iv, mItem, pos);
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
                    mArticleListener.onArticleClicked(mItem, position);
                }
            });
            binding.tlProfileImg.setOnClickListener(v -> {
                if (null != mArticleListener)
                    mArticleListener.onArticleImageClicked((ImageView) v, mItem, position);
            });
        }
    }

    public void setEnableHeader(boolean isEnable, boolean isEnableAlways, View.OnClickListener headerListener) {
        mIsEnableHeader = isEnable;
        mIsEnableHeaderAlways = isEnableAlways;
    }

    public void setEnableFooter(boolean isEnable, boolean isEnableAlways, View.OnClickListener footerListener) {
        mIsEnableFooter = isEnable;
        mIsEnableFooterAlways = isEnableAlways;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final ItemSearchHeaderBinding binding;

        HeaderViewHolder(ItemSearchHeaderBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
            mUserRecyclerView = binding.searchRv;
            UserAdapter userAdapter = new UserAdapter(mFilteredList, mListener);
            binding.searchRv.setHasFixedSize(true);
            binding.searchRv.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.searchRv.setAdapter(userAdapter);
            binding.searchRv.setRecycledViewPool(recycledViewPool);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public final ItemSearchHeaderBinding binding;

        FooterViewHolder(ItemSearchHeaderBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {

        }
    }
}