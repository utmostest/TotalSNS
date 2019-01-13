package com.enos.totalsns.profile;

import android.graphics.Color;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.databinding.ItemProfileHeaderBinding;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.userlist.OnFollowListener;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.widget.HFSupportAdapter;

import java.util.Arrays;
import java.util.List;

public class ProfileAdapter extends HFSupportAdapter {

    private List<Article> mValues;

    private OnArticleClickListener mArticleListener;
    private OnFollowListener mListener;

    private final int TYPE_TWITTER = Constants.TWITTER;
    private final int TYPE_FACEBOOK = Constants.FACEBOOK;
    private final int TYPE_INSTAGRAM = Constants.INSTAGRAM;

    private int mSnsType = Constants.DEFAULT_SNS;

    private UserInfo userInfo = null;

    private boolean isItemChanged = false;

    public ProfileAdapter(UserInfo userInfo, List<Article> list, OnArticleClickListener articleClickListener, OnFollowListener follow) {
        this.userInfo = userInfo;
        mValues = list;
        this.mArticleListener = articleClickListener;
        this.mListener = follow;
    }

    @Override
    public List<?> getItems() {
        return mValues;
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
        ItemProfileHeaderBinding itemHeaderBinding = ItemProfileHeaderBinding.inflate(inflater, parent, false);
        ProfileHeaderViewHolder holder = new ProfileHeaderViewHolder(itemHeaderBinding);
        holder.item = userInfo;
        return holder;
    }

    @Override
    public FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemProfileHeaderBinding itemFooterBinding = ItemProfileHeaderBinding.inflate(inflater, parent, false);
        return new ProfileFooterViewHolder(itemFooterBinding);
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemArticleBinding itemUserBinding = ItemArticleBinding.inflate(inflater, parent, false);
        return new ArticleViewHolder(itemUserBinding);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder vh, int pos) {
        ProfileHeaderViewHolder holder = (ProfileHeaderViewHolder) vh;
        holder.bind();
    }

    @Override
    public void onBindFooterViewHolder(FooterViewHolder vh, int pos) {
        ProfileFooterViewHolder holder = (ProfileFooterViewHolder) vh;
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

            ActivityUtils.setAutoLinkTextView(binding.getRoot().getContext(), binding.tlMessage, mItem.getMessage(), ((autoLinkMode, matchedText) -> {
                if (mArticleListener != null)
                    mArticleListener.onAutoLinkClicked(autoLinkMode, matchedText, mItem.getUrlMap());
            }));

            binding.tlTime.setText(ConvertUtils.getDateString(mItem.getPostedAt()));
            binding.tlUserName.setText(mItem.getUserName());

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

    private class ProfileHeaderViewHolder extends HeaderViewHolder {
        public final ItemProfileHeaderBinding binding;
        private UserInfo item;

        ProfileHeaderViewHolder(ItemProfileHeaderBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
            if (item == null) return;
            binding.profileAddress.setText(item.getLocation());
            binding.profileCreated.setText(ConvertUtils.getDateString(item.getCreatedAt()));
            binding.profileFollowerNum.setText(String.valueOf(item.getFollowerCount()));
            binding.profileFollowingNum.setText(String.valueOf(item.getFollowingCount()));
            binding.profileFollowingNum.setOnClickListener(v -> {
                if (mListener != null) mListener.onFollowTextClicked(item, false);
            });
            binding.profileFollowingLabel.setOnClickListener(v -> {
                if (mListener != null) mListener.onFollowTextClicked(item, false);
            });
            binding.profileFollowerNum.setOnClickListener(v -> {
                if (mListener != null) mListener.onFollowTextClicked(item, true);
            });
            binding.profileFollowerLabel.setOnClickListener(v -> {
                if (mListener != null) mListener.onFollowTextClicked(item, true);
            });
            binding.itemUserMessage.setText(item.getMessage());
            binding.itemUserName.setText(item.getUserName());
            binding.itemUserScreenId.setText(item.getUserId());

            GlideUtils.loadProfileImage(binding.getRoot().getContext(), item.getProfileImg(), binding.itemUserProfile);

            if (ConvertUtils.isStringValid(item.getProfileBackImg())) {
                GlideUtils.loadBackImage(binding.getRoot().getContext(), item.getProfileBackImg(), binding.itemUserProfileBack);
            } else if (ConvertUtils.isStringValid(item.getProfileBackColor())) {
                SingletonToast.getInstance().log("backcolor", item.getProfileBackColor());
                binding.itemUserProfileBack.setImageDrawable(null);
                binding.itemUserProfileBack.setBackgroundColor(Color.parseColor("#" + item.getProfileBackColor()));
            } else {
                binding.itemUserProfileBack.setImageResource(R.drawable.side_nav_bar);
            }
        }
    }

    private class ProfileFooterViewHolder extends FooterViewHolder {
        public final ItemProfileHeaderBinding binding;

        ProfileFooterViewHolder(ItemProfileHeaderBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {

        }
    }
}