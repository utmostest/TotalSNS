package com.enos.totalsns.search;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Search;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemSearchUserBinding;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.SingletonToast;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Search} and makes a call to the
 * specified {@link OnUserClickListener }.
 * TODO: Replace the implementation with code for your data type.
 */
public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserInfo> mFilteredList;
    private final OnUserClickListener mListener;

    public UserAdapter(List<UserInfo> items, OnUserClickListener mListener) {
        mFilteredList = items;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSearchUserBinding itemUserBinding = ItemSearchUserBinding.inflate(inflater, parent, false);
        return new UserViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder vh, final int position) {
        if (mFilteredList == null) return;
        UserViewHolder holder = (UserViewHolder) vh;
        holder.mItem = (UserInfo) mFilteredList.get(position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mFilteredList == null) return 0;
        return mFilteredList.size();
    }

    public void swapUserList(List<UserInfo> list) {
        if (list == null){
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
                    Search oldSearch = mFilteredList.get(oldItemPosition);
                    Search newSearch = list.get(newItemPosition);
                    if (oldSearch instanceof Article && newSearch instanceof Article) {
                        return ((Article) oldSearch).getArticleId() == (((Article) newSearch).getArticleId());
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
            if (ConvertUtils.isStringValid(mItem.getProfileBackImg())) {
                Glide.with(binding.getRoot().getContext())
                        .load(mItem.getProfileBackImg())
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
            } else if (ConvertUtils.isStringValid(mItem.getProfileBackColor())) {
                SingletonToast.getInstance().log("backcolor", mItem.getProfileBackColor());
                binding.itemUserProfileBack.setImageDrawable(null);
                binding.itemUserProfileBack.setBackgroundColor(Color.parseColor("#" + mItem.getProfileBackColor()));
            } else {
                binding.itemUserProfileBack.setImageResource(R.drawable.side_nav_bar);
            }
        }
    }
}