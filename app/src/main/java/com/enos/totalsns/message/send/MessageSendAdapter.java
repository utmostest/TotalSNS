package com.enos.totalsns.message.send;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemMessageSendBinding;
import com.enos.totalsns.databinding.ItemMessageSendHeaderBinding;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.GlideUtils;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link UserInfo} and makes a call to the
 * specified {@link OnUserToSendClickListener}.
 */
public class MessageSendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // TODO SNS별 뷰홀더 추가 및 화면 표시
    //    private Context mContext;
    private List<UserInfo> mValues;
    private OnUserToSendClickListener mListener;

    private final int TYPE_HEADER = -2;
    private final int TYPE_FOOTER = -1;
    private final int TYPE_TWITTER = Constants.TWITTER;
    private final int TYPE_FACEBOOK = Constants.FACEBOOK;
    private final int TYPE_INSTAGRAM = Constants.INSTAGRAM;

    private boolean mIsEnableFooter = false;
    private boolean mIsEnableHeader = false;

    private boolean mIsEnableFooterAlways = false;
    private boolean mIsEnableHeaderAlways = false;

    private int mSnsType = Constants.DEFAULT_SNS;

    public MessageSendAdapter(List<UserInfo> list, OnUserToSendClickListener listener) {
        mValues = list;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            ItemMessageSendHeaderBinding itemHeaderBinding = ItemMessageSendHeaderBinding.inflate(inflater, parent, false);
            return new HeaderViewHolder(itemHeaderBinding);
        } else if (viewType == TYPE_FOOTER) {
            ItemMessageSendHeaderBinding itemFooterBinding = ItemMessageSendHeaderBinding.inflate(inflater, parent, false);
            return new FooterViewHolder(itemFooterBinding);
        } else {
            ItemMessageSendBinding itemBinding = ItemMessageSendBinding.inflate(inflater, parent, false);
            return new ItemViewHolder(itemBinding);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int pos) {

        if (vh instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) vh;
            holder.bind();
        } else if (vh instanceof FooterViewHolder) {
            FooterViewHolder holder = (FooterViewHolder) vh;
            holder.bind();
        } else {
            int position = getActualPosition(pos);
            if (position < 0) return;

            ItemViewHolder holder = (ItemViewHolder) vh;
            holder.mItem = mValues.get(position);
            holder.bind();
        }
    }

    private int getActualPosition(int pos) {
        int size = mValues == null ? 0 : mValues.size();
        boolean isHeaderEnabled = (mIsEnableHeader && mIsEnableHeaderAlways) || (mIsEnableHeader && size > 0);

        if (size <= 0) return -1;
        int position = (pos - (isHeaderEnabled ? 1 : 0));
        return position;
    }

    @Override
    public int getItemCount() {
        int size = mValues == null ? 0 : mValues.size();
        boolean isHeaderEnabled = (mIsEnableHeader && mIsEnableHeaderAlways) || (mIsEnableHeader && size > 0);
        boolean isFooterEnabled = (mIsEnableFooter && mIsEnableFooterAlways) || (mIsEnableFooter && size > 0);

        return size + (isHeaderEnabled ? 1 : 0) + (isFooterEnabled ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        int headerPos = -1;
        int footerPos = -1;
        int size = mValues == null ? 0 : mValues.size();
        boolean isHeaderEnabled = (mIsEnableHeader && mIsEnableHeaderAlways) || (mIsEnableHeader && size > 0);
        boolean isFooterEnabled = (mIsEnableFooter && mIsEnableFooterAlways) || (mIsEnableFooter && size > 0);

        int currentType = mSnsType;

        if (isHeaderEnabled) {
            headerPos = 0;
        }
        if (isFooterEnabled) {
            footerPos = size + headerPos + 1;
        }

        if (position == headerPos) {
            return TYPE_HEADER;
        } else if (position == footerPos) {
            return TYPE_FOOTER;
        }
        return currentType;
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
                    UserInfo oldSearch = mValues.get(oldItemPosition);
                    UserInfo newSearch = list.get(newItemPosition);
                    return ConvertUtils.isUserInfoSame(oldSearch, newSearch);
                }
            });
            mValues = list;
            result.dispatchUpdatesTo(this);
        }
    }

    public void setEnableHeader(boolean isEnable, boolean isEnableAlways, View.OnClickListener headerListener) {
        mIsEnableHeader = isEnable;
        mIsEnableHeaderAlways = isEnableAlways;
    }

    public void setEnableFooter(boolean isEnable, boolean isEnableAlways, View.OnClickListener footerListener) {
        mIsEnableFooter = isEnable;
        mIsEnableFooterAlways = isEnableAlways;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        public final ItemMessageSendBinding binding;
        private UserInfo mItem;

        ItemViewHolder(ItemMessageSendBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
            binding.msUserId.setText(mItem.getUserId());
            binding.msUserName.setText(mItem.getUserName());
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getProfileImg(), binding.msProfileImg);
            binding.getRoot().setOnClickListener(v -> {
                if (mListener != null) mListener.onUserToSendClicked(mItem);
            });
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final ItemMessageSendHeaderBinding binding;

        HeaderViewHolder(ItemMessageSendHeaderBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
            binding.messageSendBtn.setOnClickListener(v -> {
                Editable editable = binding.messageSendEdit.getText();
                if (editable.toString().length() > 0) {
                    if (mListener != null) mListener.onUserToSendSearchClicked(editable.toString());
                }
            });
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public final ItemMessageSendHeaderBinding binding;

        FooterViewHolder(ItemMessageSendHeaderBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {

        }
    }
}
