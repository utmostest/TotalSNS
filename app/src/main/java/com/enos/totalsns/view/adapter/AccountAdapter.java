package com.enos.totalsns.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.view.AccountFragment;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Account} and makes a call to the
 * specified {@link AccountFragment.OnSnsAccountListener}.
 */
public class AccountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // TODO SNS별 뷰홀더 추가 및 화면 표시
    //    private Context mContext;
    private List<Account> mValues;
    private AccountFragment.OnSnsAccountListener mListener;

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

    public AccountAdapter(Context context, int snsType, AccountFragment.OnSnsAccountListener listener) {
//        mContext = context;
        mSnsType = snsType;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            //Inflating header view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_account_footer, parent, false);
            return new HeaderViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_account_footer, parent, false);
            return new FooterViewHolder(itemView);
        } else {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int pos) {

        if (vh instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) vh;

        } else if (vh instanceof FooterViewHolder) {
            FooterViewHolder holder = (FooterViewHolder) vh;
            holder.newBtn.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onNewAccountButtonClicked(mSnsType);
                }
            });

        } else {
            int size = mValues == null ? 0 : mValues.size();
            boolean isHeaderEnabled = (mIsEnableHeader && mIsEnableHeaderAlways) || (mIsEnableHeader && size > 0);

            if (size <= 0) return;
            int position = pos - (isHeaderEnabled ? 1 : 0);

            ItemViewHolder holder = (ItemViewHolder) vh;
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getId() + "");
            holder.mContentView.setText(mValues.get(position).getScreenName());

            holder.mView.setOnClickListener(v -> {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onAccountClicked(holder.mItem);
                }
            });
        }
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

    public void setAccountsList(List<Account> list) {
        mValues = list;
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
        final View mView;
        final TextView mIdView;
        final TextView mContentView;
        public Account mItem;

        ItemViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        TextView headerTitle;

        HeaderViewHolder(View view) {
            super(view);
            mView = view;
//            headerTitle = (TextView) view.findViewById(R.id.header_text);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        Button newBtn;

        FooterViewHolder(View view) {
            super(view);
            mView = view;
            newBtn = view.findViewById(R.id.fragment_account_new_btn);
//            footerText = (TextView) view.findViewById(R.id.footer_text);
        }
    }
}
