package com.enos.totalsns.userlist;

import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemUserBinding;
import com.enos.totalsns.util.CompareUtils;
import com.enos.totalsns.util.GlideUtils;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserInfo> mValues;
    private final OnUserClickListener mListener;

    public UserAdapter(List<UserInfo> items, OnUserClickListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemUserBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mValues == null) return;
        holder.bind(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    public List<UserInfo> getUserList() {
        return mValues;
    }

    public void swapUserList(List<UserInfo> list) {
        if (mValues == null || list == null) {
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
                    return CompareUtils.isUserInfoSame(mValues.get(oldItemPosition), list.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return CompareUtils.isUserInfoEqual(mValues.get(oldItemPosition), list.get(newItemPosition));
                }
            });
            mValues = list;
            result.dispatchUpdatesTo(this);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ItemUserBinding binding;

        ViewHolder(ItemUserBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind(UserInfo mItem) {
            binding.fUserId.setText(mItem.getUserId());
            binding.fUserName.setText(mItem.getUserName());
            binding.fMessage.setText(mItem.getMessage());
            binding.fMessage.setText(mItem.getMessage());
            binding.fFollowBtn.setVisibility(mItem.getFollowInfo() != null && mItem.getFollowInfo().isMe() ? View.GONE : View.VISIBLE);
            binding.fFollowBtn.setText(mItem.getFollowInfo() != null && mItem.getFollowInfo().isFollowing() ?
                    R.string.title_following : R.string.do_follow);
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getProfileImg(), binding.fProfileImg);

            binding.getRoot().setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onUserItemClicked(binding, mItem);
                }
            });
            binding.fFollowBtn.setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onFollowButtonClicked(mItem);
                }
            });
        }
    }
}
