package com.enos.totalsns.profile;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.enos.totalsns.LayoutLoad;
import com.enos.totalsns.R;
import com.enos.totalsns.custom.HFSupportAdapter;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.databinding.ItemProfileHeaderBinding;
import com.enos.totalsns.image.ImageActivity;
import com.enos.totalsns.listener.OnArticleClickListener;
import com.enos.totalsns.listener.OnFollowBtnClickListener;
import com.enos.totalsns.listener.OnFollowListener;
import com.enos.totalsns.util.AutoLinkTextUtils;
import com.enos.totalsns.util.CompareUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.StringUtils;
import com.enos.totalsns.util.TimeUtils;

import java.util.List;

public class ProfileAdapter extends HFSupportAdapter {

    private List<Article> mValues;

    private OnArticleClickListener mArticleListener;
    private OnFollowListener mListener;
    private OnFollowBtnClickListener followBtnClickListener;
    private LayoutLoad layoutLoad;

    private final int TYPE_TWITTER = Constants.TWITTER;
    private final int TYPE_FACEBOOK = Constants.FACEBOOK;
    private final int TYPE_INSTAGRAM = Constants.INSTAGRAM;

    private int mSnsType = Constants.DEFAULT_SNS;

    private UserInfo userInfo = null;

    private boolean isItemChanged = false;

    public ProfileAdapter(UserInfo userInfo, List<Article> list, OnArticleClickListener articleClickListener,
                          OnFollowListener follow, OnFollowBtnClickListener followBtnListener, LayoutLoad load) {
        this.userInfo = userInfo;
        mValues = list;
        this.mArticleListener = articleClickListener;
        this.mListener = follow;
        this.layoutLoad = load;
        this.followBtnClickListener = followBtnListener;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
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
        holder.bind(userInfo);
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
        holder.bind(mValues.get(position));
    }

    @Override
    public int getYourItemViewType(int position) {
        return 0;
    }

    public void swapTimelineList(List<Article> list) {
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
                    return CompareUtils.isArticleSame(mValues.get(oldItemPosition), list.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return CompareUtils.isArticleEqual(mValues.get(oldItemPosition), list.get(newItemPosition));
                }
            }, true);
            mValues = list;
            result.dispatchUpdatesTo(this);
        }
        isItemChanged = true;
    }

    private class ArticleViewHolder extends ItemViewHolder {
        public final ItemArticleBinding binding;

        ArticleViewHolder(ItemArticleBinding view) {
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
            //Log.i("bind", "urlSize : " + urlSize + ", imgUrls : " + Arrays.toString(imgUrls));
            binding.imageContainer.setOnImageClickedListener((iv, pos) -> {
                if (mArticleListener != null)
                    mArticleListener.onArticleImageClicked(iv, mItem, pos);
            });
            if (hasImage) {
                binding.imageContainer.loadImageViewsWithGlide(Glide.with(binding.imageContainer.getContext()), imgUrls);
            }

            binding.tlUserId.setText(mItem.getUserId());

            AutoLinkTextUtils.set(binding.getRoot().getContext(), binding.tlMessage, mItem.getMessage(), ((autoLinkMode, matchedText) -> {
                if (mArticleListener != null)
                    mArticleListener.onAutoLinkClicked(autoLinkMode, matchedText, mItem.getUrlMap());
            }));

            binding.tlTime.setText(TimeUtils.getDateString(mItem.getPostedAt()));
            binding.tlUserName.setText(mItem.getUserName());

            binding.getRoot().setOnClickListener(v -> {
                if (null != mArticleListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mArticleListener.onArticleClicked(binding, mItem);
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

        ProfileHeaderViewHolder(ItemProfileHeaderBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind(UserInfo item) {
            if (item == null) return;
            binding.profileAddress.setText(item.getLocation());
            binding.profileCreated.setText(TimeUtils.getDateString(item.getCreatedAt()));
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
            binding.itemUserFollowBtn.setVisibility(item.getFollowInfo() != null && item.getFollowInfo().isMe() ? View.GONE : View.VISIBLE);
            binding.itemUserFollowBtn.setText(item.getFollowInfo() != null && item.getFollowInfo().isFollowing() ?
                    R.string.title_following : (item.isFollowReqSend() ? R.string.wait_follow : R.string.do_follow));
            binding.itemUserFollowBtn.setEnabled(!item.isFollowReqSend());
            binding.itemUserFollowBtn.setOnClickListener(v -> {
                if (followBtnClickListener != null)
                    followBtnClickListener.onFollowButtonClicked(item);
            });
            binding.itemUserMessage.setText(item.getMessage());
            binding.itemUserName.setText(item.getUserName());
            binding.itemUserScreenId.setText(item.getUserId());
            binding.itemUserProfile.setOnClickListener(v -> {
                ImageActivity.start((AppCompatActivity) binding.getRoot().getContext(), new String[]{item.getProfileImg()}, 0);
            });

            GlideUtils.loadProfileImage(binding.getRoot().getContext(), item.getProfileImg(), binding.itemUserProfile, new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    setLayoutLoaded(LayoutLoad.IMAGE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    setLayoutLoaded(LayoutLoad.IMAGE);
                    return false;
                }
            });

            if (StringUtils.isStringValid(item.getProfileBackImg())) {
                binding.itemUserProfileBack.setOnClickListener(v -> {
                    ImageActivity.start((AppCompatActivity) binding.getRoot().getContext(), new String[]{item.getProfileBackImg()}, 0);
                });

                GlideUtils.loadBackImage(binding.getRoot().getContext(), item.getProfileBackImg(), binding.itemUserProfileBack,
                        new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                setLayoutLoaded(LayoutLoad.BACK);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                setLayoutLoaded(LayoutLoad.BACK);
                                return false;
                            }
                        });
            } else if (StringUtils.isStringValid(item.getProfileBackColor())) {
                SingletonToast.getInstance().log("backcolor", item.getProfileBackColor());
                binding.itemUserProfileBack.setImageDrawable(null);
                binding.itemUserProfileBack.setBackgroundColor(Color.parseColor("#" + item.getProfileBackColor()));
                setLayoutLoaded(LayoutLoad.BACK);
            } else {
                setLayoutLoaded(LayoutLoad.BACK);
            }
        }
    }

    private void setLayoutLoaded(int target) {
        if (layoutLoad != null) {
            if (target == LayoutLoad.IMAGE) {
                Log.i("layout", "image");
                layoutLoad.setImgLoadAndCallbackIfLaoded();
            } else {
                Log.i("layout", "back");
                layoutLoad.setBackLoadAndCallbackIfLaoded();
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