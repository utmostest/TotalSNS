package com.enos.totalsns.message.detail;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.databinding.ItemMessageDetailInBinding;
import com.enos.totalsns.databinding.ItemMessageDetailOutBinding;
import com.enos.totalsns.listener.OnMessageClickListener;
import com.enos.totalsns.util.CompareUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.TimeUtils;

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
            holder.bind(mValues.get(position));
        } else if (vh instanceof OutViewHolder) {
            OutViewHolder holder = (OutViewHolder) vh;
            holder.bind(mValues.get(position));
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

    public class InViewHolder extends RecyclerView.ViewHolder {
        public final ItemMessageDetailInBinding binding;

        InViewHolder(ItemMessageDetailInBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind(Message mItem) {
            binding.messageItemMsg.setText(mItem.getMessage());
            binding.messageItemTime.setText(TimeUtils.getDateString(mItem.getCreatedAt()));
            binding.getRoot().setOnClickListener(v -> {
                if (mListener != null) mListener.onMessageClicked(mItem);
            });
        }
    }

    public class OutViewHolder extends RecyclerView.ViewHolder {
        private ItemMessageDetailOutBinding binding;

        public OutViewHolder(ItemMessageDetailOutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message mItem) {
            binding.messageItemMsg.setText(mItem.getMessage());
            binding.messageItemTime.setText(TimeUtils.getDateString(mItem.getCreatedAt()));
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getSenderProfile(), binding.messageItemProfile, R.drawable.ic_account);
            binding.getRoot().setOnClickListener(v -> {
                if (mListener != null) mListener.onMessageClicked(mItem);
            });
        }
    }
}
