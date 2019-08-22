package com.enos.totalsns.userlist;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemUserBinding;
import com.enos.totalsns.listener.OnFollowBtnClickListener;
import com.enos.totalsns.listener.OnUserClickListener;
import com.enos.totalsns.util.CompareUtils;
import com.enos.totalsns.util.GlideUtils;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserInfo> mValues;
    private final OnUserClickListener mListener;
    private OnFollowBtnClickListener followBtnClickListener;

    public UserAdapter(List<UserInfo> items, OnUserClickListener listener, OnFollowBtnClickListener followBtnListener) {
        mValues = items;
        mListener = listener;
        followBtnClickListener = followBtnListener;
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
            binding.fFollowBtn.setEnabled(!mItem.isFollowReqSend());
            binding.fFollowBtn.setText(mItem.getFollowInfo() != null && mItem.getFollowInfo().isFollowing() ?
                    R.string.title_following : (mItem.isFollowReqSend() ? R.string.wait_follow : R.string.do_follow));
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getProfileImg(), binding.fProfileImg);

            binding.getRoot().setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onUserItemClicked(binding, mItem);
                }
            });
            binding.fFollowBtn.setOnClickListener(v -> {
                if (null != followBtnClickListener) {
                    followBtnClickListener.onFollowButtonClicked(mItem);
                }
            });
        }
    }
}
