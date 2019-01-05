package com.enos.totalsns.accounts;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.ItemAccountBinding;
import com.enos.totalsns.databinding.ItemAccountFooterBinding;
import com.enos.totalsns.util.GlideUtils;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Account} and makes a call to the
 * specified {@link OnSnsAccountListener}.
 */
public class AccountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // TODO SNS별 뷰홀더 추가 및 화면 표시
    //    private Context mContext;
    private List<Account> mValues;
    private OnSnsAccountListener mListener;

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

    public AccountsAdapter(Context context, int snsType, OnSnsAccountListener listener) {
        mSnsType = snsType;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            ItemAccountFooterBinding itemAccountFooterBinding = ItemAccountFooterBinding.inflate(inflater, parent, false);
            return new HeaderViewHolder(itemAccountFooterBinding);
        } else if (viewType == TYPE_FOOTER) {
            ItemAccountFooterBinding itemAccountFooterBinding = ItemAccountFooterBinding.inflate(inflater, parent, false);
            return new FooterViewHolder(itemAccountFooterBinding);
        } else {
            ItemAccountBinding itemAccountBinding = ItemAccountBinding.inflate(inflater, parent, false);
            return new ItemViewHolder(itemAccountBinding);
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

    public void swapAccountsList(List<Account> list) {
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
                    return mValues.get(oldItemPosition).getId() == list.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Account oldAccount = mValues.get(oldItemPosition);
                    Account newAccount = list.get(newItemPosition);
                    return oldAccount.getId() == newAccount.getId() &&
                            oldAccount.getSnsType() == newAccount.getSnsType() &&
                            oldAccount.isCurrent() == newAccount.isCurrent() &&
                            oldAccount.getName().equals(newAccount.getName()) &&
                            oldAccount.getOauthSecret().equals(newAccount.getOauthSecret()) &&
                            oldAccount.getOauthKey().equals(newAccount.getOauthKey()) &&
                            oldAccount.getProfileImage().equals(newAccount.getProfileImage()) &&
                            oldAccount.getScreenName().equals(newAccount.getScreenName());
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
        public final ItemAccountBinding binding;
        private Account mItem;

        ItemViewHolder(ItemAccountBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
            binding.accUserId.setText(mItem.getScreenName());
            binding.accUserName.setText(mItem.getName());
            GlideUtils.loadProfileImage(binding.getRoot().getContext(),mItem.getProfileImage(),binding.accProfileImg);

            binding.getRoot().setOnClickListener(v -> {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onAccountClicked(mItem);
                }
            });
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final ItemAccountFooterBinding binding;

        HeaderViewHolder(ItemAccountFooterBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public final ItemAccountFooterBinding binding;

        FooterViewHolder(ItemAccountFooterBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
            binding.fragmentAccountNewBtn.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onNewAccountButtonClicked(mSnsType);
                }
            });
        }
    }
}
