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
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.GlideUtils;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Message} and makes a call to the
 * specified {@link OnMessageClickListener}.
 * TODO: Replace the implementation with code for your data type.
 */
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
        if (mValues == null || list==null) {
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
                    return mValues.get(oldItemPosition).getUserDmId().equals(list.get(newItemPosition).getUserDmId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Message oldMessage = mValues.get(oldItemPosition);
                    Message newMessage = list.get(newItemPosition);
                    return oldMessage.getCreatedAt() == newMessage.getCreatedAt() &&
                            oldMessage.getSnsType() == newMessage.getSnsType() &&
                            oldMessage.getMessageId() == newMessage.getMessageId() &&
                            oldMessage.getReceiverId() == newMessage.getReceiverId() &&
                            oldMessage.getSenderId() == newMessage.getSenderId() &&
                            oldMessage.getTableUserId() == newMessage.getTableUserId() &&
                            oldMessage.getSenderTableId() == newMessage.getSenderTableId() &&
                            oldMessage.getMessage().equals(newMessage.getMessage()) &&
                            oldMessage.getSenderName().equals(newMessage.getSenderName()) &&
                            oldMessage.getSenderScreenId().equals(newMessage.getSenderScreenId()) &&
                            oldMessage.getSenderProfile().equals(newMessage.getSenderProfile()) &&
                            oldMessage.getUserDmId().equals(newMessage.getUserDmId());
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
            ActivityUtils.setAutoLinkTextView(binding.getRoot().getContext(), binding.mMessage, mItem.getMessage(), null);
            binding.mMessage.setText(mItem.getMessage());
            binding.mTime.setText(ConvertUtils.getDateString(mItem.getCreatedAt()));
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getSenderProfile(), binding.mProfileImg);

            binding.getRoot().setOnClickListener(v -> {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onMessageClicked(mItem);
                }
            });
        }
    }
}
