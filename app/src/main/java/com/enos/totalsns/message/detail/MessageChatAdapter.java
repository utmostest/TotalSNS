package com.enos.totalsns.message.detail;

import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.databinding.ItemMessageDetailInBinding;
import com.enos.totalsns.databinding.ItemMessageDetailOutBinding;
import com.enos.totalsns.message.OnMessageClickListener;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.GlideUtils;

import java.util.List;

public class MessageChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> mValues;
    private final OnMessageClickListener mListener;

    private final int VIEWTYPE_NONE = 0;
    private final int VIEWTYPE_OUT = 1;
    private final int VIEWTYPE_IN = 2;

    public MessageChatAdapter(List<Message> items, OnMessageClickListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEWTYPE_OUT) {
            ItemMessageDetailOutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_message_detail_out, parent, false);
            return new MessageChatAdapter.OutViewHolder(binding);
        } else {
            ItemMessageDetailInBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_message_detail_in, parent, false);
            return new MessageChatAdapter.InViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position) {
        if (mValues == null) return;
        if (vh instanceof InViewHolder) {
            InViewHolder holder = (InViewHolder) vh;
            holder.mItem = mValues.get(position);
            holder.bind();
        } else if (vh instanceof OutViewHolder) {
            OutViewHolder holder = (OutViewHolder) vh;
            holder.mItem = mValues.get(position);
            holder.bind();
        }
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mValues == null || mValues.size() <= 0) return VIEWTYPE_NONE;
        Message message = mValues.get(position);
        return message.getTableUserId() == message.getSenderId() ? VIEWTYPE_IN : VIEWTYPE_OUT;
    }

    public void swapMessageList(List<Message> list) {
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

    public class InViewHolder extends RecyclerView.ViewHolder {
        public final ItemMessageDetailInBinding binding;
        private Message mItem;

        InViewHolder(ItemMessageDetailInBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
            binding.messageItemMsg.setText(mItem.getMessage());
            binding.messageItemTime.setText(ConvertUtils.getDateString(mItem.getCreatedAt()));
            binding.getRoot().setOnClickListener(v -> {
                if (mListener != null) mListener.onMessageClicked(mItem);
            });
        }
    }

    public class OutViewHolder extends RecyclerView.ViewHolder {
        private ItemMessageDetailOutBinding binding;
        private Message mItem;

        public OutViewHolder(ItemMessageDetailOutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind() {
            binding.messageItemMsg.setText(mItem.getMessage());
            binding.messageItemTime.setText(ConvertUtils.getDateString(mItem.getCreatedAt()));
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getSenderProfile(), binding.messageItemProfile, R.drawable.ic_account_circle_black_36dp);
            binding.getRoot().setOnClickListener(v -> {
                if (mListener != null) mListener.onMessageClicked(mItem);
            });
            binding.messageItemProfile.setOnClickListener((v) -> {
                if (mListener != null) mListener.onMessageProfileClicked(mItem.getSenderTableId());
            });
        }
    }
}
