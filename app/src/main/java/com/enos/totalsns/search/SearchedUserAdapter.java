package com.enos.totalsns.search;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemSearchUserBinding;
import com.enos.totalsns.userlist.OnUserClickListener;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.SingletonToast;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link UserInfo} and makes a call to the
 * specified {@link OnUserClickListener }.
 * TODO: Replace the implementation with code for your data type.
 */
public class SearchedUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserInfo> mFilteredList;
    private final OnUserClickListener mListener;

    public SearchedUserAdapter(List<UserInfo> items, OnUserClickListener mListener) {
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
        if (list == null || mFilteredList == null) {
            mFilteredList = list;
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
                    UserInfo oldSearch = mFilteredList.get(oldItemPosition);
                    UserInfo newSearch = list.get(newItemPosition);
                    return oldSearch.getLongUserId() == newSearch.getLongUserId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    UserInfo oldSearch = mFilteredList.get(oldItemPosition);
                    UserInfo newSearch = list.get(newItemPosition);
                    return ConvertUtils.isUserInfoSame(oldSearch, newSearch);
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
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getProfileImg(), binding.itemUserProfile);
            if (ConvertUtils.isStringValid(mItem.getProfileBackImg())) {
                GlideUtils.loadBackImage(binding.getRoot().getContext(), mItem.getProfileBackImg(), binding.itemUserProfileBack);
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