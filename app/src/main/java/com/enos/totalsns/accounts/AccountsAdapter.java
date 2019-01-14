package com.enos.totalsns.accounts;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.ItemAccountBinding;
import com.enos.totalsns.databinding.ItemAccountFooterBinding;
import com.enos.totalsns.util.CompareUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.custom.HFSupportAdapter;

import java.util.List;

public class AccountsAdapter extends HFSupportAdapter {

    // TODO SNS별 뷰홀더 추가 및 화면 표시
    private List<Account> mValues;
    private OnSnsAccountListener mListener;

    private final int TYPE_TWITTER = Constants.TWITTER;
    private final int TYPE_FACEBOOK = Constants.FACEBOOK;
    private final int TYPE_INSTAGRAM = Constants.INSTAGRAM;

    private int mSnsType = Constants.DEFAULT_SNS;

    private boolean isItemChanged = false;

    public AccountsAdapter(Context context, int snsType, OnSnsAccountListener listener) {
        mSnsType = snsType;
        mListener = listener;
    }

    @Override
    public List<?> getItems() {
        return mValues;
    }

    @Override
    public boolean isItemChanged() {
        return isItemChanged;
    }

    @Override
    public void setIsItemChanged(boolean changed) {
        isItemChanged = changed;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemAccountFooterBinding itemAccountFooterBinding = ItemAccountFooterBinding.inflate(inflater, parent, false);
        return new AccountHeaderViewHolder(itemAccountFooterBinding);
    }

    @Override
    public FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemAccountFooterBinding itemAccountFooterBinding = ItemAccountFooterBinding.inflate(inflater, parent, false);
        return new AccountFooterViewHolder(itemAccountFooterBinding);
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemAccountBinding itemAccountBinding = ItemAccountBinding.inflate(inflater, parent, false);
        return new AccountItemViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder vh, int pos) {
        AccountHeaderViewHolder holder = (AccountHeaderViewHolder) vh;
        holder.bind();
    }

    @Override
    public void onBindFooterViewHolder(FooterViewHolder vh, int pos) {
        AccountFooterViewHolder holder = (AccountFooterViewHolder) vh;
        holder.bind();
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder vh, int position) {
        AccountItemViewHolder holder = (AccountItemViewHolder) vh;
        holder.mItem = mValues.get(position);
        holder.bind();
    }

    @Override
    public int getYourItemViewType(int position) {
        return 0;
    }

    public void swapAccountsList(List<Account> list) {
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
                    return CompareUtils.isAccountSame(mValues.get(oldItemPosition), list.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return CompareUtils.isAccountEqual(mValues.get(oldItemPosition), list.get(newItemPosition));
                }
            });
            mValues = list;
            result.dispatchUpdatesTo(this);
        }
        isItemChanged = true;
    }

    private class AccountItemViewHolder extends ItemViewHolder {
        public final ItemAccountBinding binding;
        private Account mItem;

        AccountItemViewHolder(ItemAccountBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
            binding.accUserId.setText(mItem.getScreenName());
            binding.accUserName.setText(mItem.getName());
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getProfileImage(), binding.accProfileImg);

            binding.getRoot().setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onAccountClicked(mItem);
                }
            });
        }
    }

    private class AccountHeaderViewHolder extends HeaderViewHolder {
        public final ItemAccountFooterBinding binding;

        AccountHeaderViewHolder(ItemAccountFooterBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
        }
    }

    private class AccountFooterViewHolder extends FooterViewHolder {
        public final ItemAccountFooterBinding binding;

        AccountFooterViewHolder(ItemAccountFooterBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
        }
    }
}
