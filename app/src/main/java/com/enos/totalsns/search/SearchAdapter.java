package com.enos.totalsns.search;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Search;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemArticleBinding;
import com.enos.totalsns.databinding.ItemSearchUserBinding;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ConvertUtils;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Search} and makes a call to the
 * specified {@link OnUserClickListener }. {@link OnArticleClickListener}
 * TODO: Replace the implementation with code for your data type.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Search> mFilteredList;
    private final OnUserClickListener mListener;
    private final OnArticleClickListener mArticleListener;

    private final int VIEWTYPE_USER = 1;
    private final int VIEWTYPE_ARTICLE = 2;

    public SearchAdapter(List<Search> items, OnArticleClickListener listener, OnUserClickListener mListener) {
        mFilteredList = items;
        this.mListener = mListener;
        this.mArticleListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEWTYPE_USER) {
            ItemSearchUserBinding itemUserBinding = ItemSearchUserBinding.inflate(inflater, parent, false);
            return new UserViewHolder(itemUserBinding);
        } else {
            ItemArticleBinding itemArticleBinding = ItemArticleBinding.inflate(inflater, parent, false);
            return new ArticleViewModel(itemArticleBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder vh, final int position) {
        if (mFilteredList == null) return;
        if (vh instanceof UserViewHolder) {
            UserViewHolder holder = (UserViewHolder) vh;
            holder.mItem = (UserInfo) mFilteredList.get(position);
            holder.bind(position);
        } else if (vh instanceof ArticleViewModel) {
            ArticleViewModel holder = (ArticleViewModel) vh;
            holder.mItem = (Article) mFilteredList.get(position);
            holder.bind(position);
        }
    }

    public void swapTimelineList(List<Search> list) {
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
                    Search oldSearch = mFilteredList.get(oldItemPosition);
                    Search newSearch = mFilteredList.get(newItemPosition);
                    if (oldSearch instanceof Article && newSearch instanceof Article) {
                        return ((Article) oldSearch).getTablePlusArticleId().equals(((Article) newSearch).getTablePlusArticleId());
                    } else if (oldSearch instanceof UserInfo && newSearch instanceof UserInfo) {
                        return ((UserInfo) oldSearch).getLongUserId() == ((UserInfo) newSearch).getLongUserId();
                    } else {
                        return false;
                    }
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Search oldSearch = mFilteredList.get(oldItemPosition);
                    Search newSearch = list.get(newItemPosition);
                    return ConvertUtils.isSearchSame(oldSearch, newSearch);
                }
            }, true);
            mFilteredList = list;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mFilteredList == null) return 0;
        if (mFilteredList.get(position) instanceof Article) {
            return VIEWTYPE_ARTICLE;
        } else if (mFilteredList.get(position) instanceof UserInfo) {
            return VIEWTYPE_USER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (mFilteredList == null) return 0;
        return mFilteredList.size();
    }

    private class ArticleViewModel extends RecyclerView.ViewHolder {
        public final ItemArticleBinding binding;
        private Article mItem;

        ArticleViewModel(ItemArticleBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind(int position) {
            Glide.with(binding.getRoot().getContext())
                    .load(mItem.getProfileImg())
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.drawable.ic_account_circle_black_48dp)
                                    .dontTransform()
                                    .optionalCircleCrop()
                    )
                    .transition(
                            new DrawableTransitionOptions()
                                    .crossFade(Constants.CROSS_FADE_MILLI)
                    )
                    .into(binding.tlProfileImg);

            final String[] imgUrls = mItem.getImageUrls();
            int urlSize = ConvertUtils.getActualSize(imgUrls);
            boolean hasImage = urlSize > 0;
            binding.imageContainer.setVisibility(hasImage ? View.VISIBLE : View.GONE);
            binding.imageContainer.setImageCount(urlSize);
            binding.imageContainer.setOnImageClickedListener((iv, pos) -> {
                if (mArticleListener != null)
                    mArticleListener.onArticleImageClicked(iv, mItem, pos);
            });
            if (hasImage) {
                binding.imageContainer.loadImageViewsWithGlide(Glide.with(binding.imageContainer.getContext()), imgUrls);
            }

            binding.tlUserId.setText(mItem.getUserId());

            ActivityUtils.setAutoLinkTextView(binding.getRoot().getContext(), binding.tlMessage, mItem);

            binding.tlTime.setText(ConvertUtils.getDateString(mItem.getPostedAt()));
            binding.tlUserName.setText(mItem.getUserName());

            binding.getRoot().setOnClickListener(v -> {
                if (null != mArticleListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mArticleListener.onArticleClicked(mItem, position);
                }
            });
        }
    }

    private class UserViewHolder extends RecyclerView.ViewHolder {
        public final ItemSearchUserBinding binding;
        private UserInfo mItem;

        UserViewHolder(ItemSearchUserBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind(int position) {
            binding.getRoot().setOnClickListener((view) -> {
                if (mListener != null) mListener.onUserItemClicked(mItem);
            });
            binding.itemUserFollowBtn.setOnClickListener((view) -> {
                if (mListener != null) mListener.onFollowButtonClicked(mItem);
            });
            binding.itemUserMessage.setText(mItem.getMessage());
            binding.itemUserName.setText(mItem.getUserName());
            binding.itemUserScreenId.setText(mItem.getUserId());
            Glide.with(binding.getRoot().getContext())
                    .load(mItem.getProfileImg())
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.drawable.ic_account_circle_black_48dp)
                                    .dontTransform()
                                    .centerCrop()
                                    .optionalCircleCrop()
                    )
                    .transition(
                            new DrawableTransitionOptions()
                                    .crossFade(Constants.CROSS_FADE_MILLI)
                    )
                    .into(binding.itemUserProfile);
            Glide.with(binding.getRoot().getContext())
                    .load(mItem.getProfileImg())
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.drawable.side_nav_bar)
                                    .dontTransform()
                                    .centerCrop()
                    )
                    .transition(
                            new DrawableTransitionOptions()
                                    .crossFade(Constants.CROSS_FADE_MILLI)
                    )
                    .into(binding.itemUserProfileBack);
        }
    }
}

