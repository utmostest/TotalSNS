package com.enos.totalsns.follow;

import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemFollowBinding;
import com.enos.totalsns.search.OnUserClickListener;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.GlideUtils;

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
    private List<UserInfo> mValues;
    private final OnUserClickListener mListener;

    public FollowAdapter(List<UserInfo> items, OnUserClickListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFollowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_follow, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mValues == null) return;
        holder.mItem = mValues.get(position);
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    public void swapUserList(List<UserInfo> list) {
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
                    return mValues.get(oldItemPosition).getLongUserId() == list.get(newItemPosition).getLongUserId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    UserInfo oldItem = mValues.get(oldItemPosition);
                    UserInfo newItem = list.get(newItemPosition);
                    return oldItem.getCreatedAt() == newItem.getCreatedAt() &&
                            oldItem.getSnsType() == newItem.getSnsType() &&
                            oldItem.getLongUserId() == newItem.getLongUserId() &&
                            oldItem.getFollowerCount() == newItem.getFollowerCount() &&
                            oldItem.getFollowingCount() == newItem.getFollowingCount() &&
                            ConvertUtils.isStringEqual(oldItem.getUserName(), newItem.getUserName()) &&
                            ConvertUtils.isStringEqual(oldItem.getUserId(), newItem.getUserId()) &&
                            ConvertUtils.isStringEqual(oldItem.getProfileBackImg(), newItem.getProfileBackImg()) &&
                            ConvertUtils.isStringEqual(oldItem.getProfileImg(), newItem.getProfileImg()) &&
                            ConvertUtils.isStringEqual(oldItem.getProfileBackColor(), newItem.getProfileBackColor()) &&
                            ConvertUtils.isStringEqual(oldItem.getEmail(), newItem.getEmail()) &&
                            ConvertUtils.isStringEqual(oldItem.getLocation(), newItem.getLocation()) &&
                            ConvertUtils.isStringEqual(oldItem.getMessage(), newItem.getMessage()) &&
                            ConvertUtils.isArticleSame(oldItem.getLastArticle(), newItem.getLastArticle());
                }
            });
            mValues = list;
            result.dispatchUpdatesTo(this);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ItemFollowBinding binding;
        private UserInfo mItem;

        ViewHolder(ItemFollowBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
            binding.fUserId.setText(mItem.getUserId());
            binding.fUserName.setText(mItem.getUserName());
            binding.fMessage.setText(mItem.getMessage());
            binding.fMessage.setText(mItem.getMessage());
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getProfileImg(), binding.fProfileImg);

            binding.getRoot().setOnClickListener(v -> {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onUserItemClicked(mItem);
                }
            });
            binding.itemUserFollowBtn.setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onFollowButtonClicked(mItem);
                }
            });
        }
    }
}
