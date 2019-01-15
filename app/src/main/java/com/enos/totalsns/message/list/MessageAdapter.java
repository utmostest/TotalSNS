package com.enos.totalsns.message.list;

import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.databinding.ItemMessageBinding;
import com.enos.totalsns.message.OnMessageClickListener;
import com.enos.totalsns.util.AutoLinkTextUtils;
import com.enos.totalsns.util.CompareUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.TimeUtils;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mValues;
    private final OnMessageClickListener mListener;

    public MessageAdapter(List<Message> items, OnMessageClickListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemMessageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_message, parent, false);
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

    public void swapMessageList(List<Message> list) {
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
                    return CompareUtils.isMessageSame(mValues.get(oldItemPosition), list.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return CompareUtils.isMessageEqual(mValues.get(oldItemPosition), list.get(newItemPosition));
                }
            });
            mValues = list;
            result.dispatchUpdatesTo(this);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ItemMessageBinding binding;
        private Message mItem;

        ViewHolder(ItemMessageBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
            binding.mUserId.setText(mItem.getSenderScreenId());
            binding.mUserName.setText(mItem.getSenderName());
            binding.mMessage.setText(mItem.getMessage());
            binding.mTime.setText(TimeUtils.getDateString(mItem.getCreatedAt()));
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getSenderProfile(), binding.mProfileImg);

            binding.getRoot().setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onMessageClicked(mItem);
                }
            });
        }
    }
}
