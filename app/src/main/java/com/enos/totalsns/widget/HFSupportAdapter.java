package com.enos.totalsns.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * adapter that supports header and footer
 */
public abstract class HFSupportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_HEADER = -2;
    private final int TYPE_FOOTER = -1;

    private boolean mIsEnableFooter = false;
    private boolean mIsEnableHeader = false;

    private boolean mIsEnableFooterAlways = false;
    private boolean mIsEnableHeaderAlways = false;

    private List<?> mValues = null;

    private void checkItems() {
        if (mValues == null || isItemChanged()) {
            mValues = getItems();
            setIsItemChanged(false);
        }
    }

    /**
     * you must return your item list here
     **/
    public abstract List<?> getItems();

    /**
     * set is your item changed here and set true when your item list changed
     */
    public abstract boolean isItemChanged();

    /**
     * set your itemchanged variable to this value.
     **/
    public abstract void setIsItemChanged(boolean changed);

    /**
     * don't override it
     **/
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return onCreateHeaderViewHolder(parent, viewType);
        } else if (viewType == TYPE_FOOTER) {
            return onCreateFooterViewHolder(parent, viewType);
        } else {
            return onCreateItemViewHolder(parent, viewType);
        }
    }

    /**
     * create your header viewholder
     **/
    public abstract HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType);

    /**
     * create your footer viewholder
     **/
    public abstract FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType);

    /**
     * create your item viewholder
     **/
    public abstract ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    /**
     * don't override it
     **/
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int pos) {
        int position = getActualPosition(pos);
        if (vh instanceof HeaderViewHolder) {
            onBindHeaderViewHolder((HeaderViewHolder) vh, pos);
        } else if (vh instanceof FooterViewHolder) {
            onBindFooterViewHolder((FooterViewHolder) vh, pos);
        } else {
            if (position < 0) return;
            onBindItemViewHolder((ItemViewHolder) vh, position);
        }
    }

    /**
     * bind your header viewholder
     **/
    public abstract void onBindHeaderViewHolder(HeaderViewHolder vh, int pos);

    /**
     * bind your footer viewholder
     **/
    public abstract void onBindFooterViewHolder(FooterViewHolder vh, int pos);

    /**
     * bind your item viewholder
     **/
    public abstract void onBindItemViewHolder(ItemViewHolder vh, int pos);

    /**
     * don't override it
     **/
    private int getActualPosition(int pos) {
        checkItems();
        int size = mValues == null ? 0 : mValues.size();
        boolean isHeaderEnabled = (mIsEnableHeader && mIsEnableHeaderAlways) || (mIsEnableHeader && size > 0);

        if (size <= 0) return -1;
        int position = (pos - (isHeaderEnabled ? 1 : 0));
        return position;
    }

    /**
     * don't override it
     **/
    @Override
    public int getItemCount() {
        checkItems();
        int size = mValues == null ? 0 : mValues.size();
        boolean isHeaderEnabled = (mIsEnableHeader && mIsEnableHeaderAlways) || (mIsEnableHeader && size > 0);
        boolean isFooterEnabled = (mIsEnableFooter && mIsEnableFooterAlways) || (mIsEnableFooter && size > 0);

        return size + (isHeaderEnabled ? 1 : 0) + (isFooterEnabled ? 1 : 0);
    }

    /**
     * don't override it
     **/
    @Override
    public int getItemViewType(int position) {
        int realPosition = getActualPosition(position);
        int headerPos = -1;
        int footerPos = -1;
        int size = mValues == null ? 0 : mValues.size();
        boolean isHeaderEnabled = (mIsEnableHeader && mIsEnableHeaderAlways) || (mIsEnableHeader && size > 0);
        boolean isFooterEnabled = (mIsEnableFooter && mIsEnableFooterAlways) || (mIsEnableFooter && size > 0);

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
        return getYourItemViewType(realPosition);
    }

    /**
     * just implement like original getItemViewType
     * but do not use -1, -2 : these are used at header, footer viewtype
     **/
    public abstract int getYourItemViewType(int position);

    /**
     * don't override it
     **/
    public void setEnableHeader(boolean isEnable, boolean isEnableAlways) {
        mIsEnableHeader = isEnable;
        mIsEnableHeaderAlways = isEnableAlways;
    }

    /**
     * don't override it
     **/
    public void setEnableFooter(boolean isEnable, boolean isEnableAlways) {
        mIsEnableFooter = isEnable;
        mIsEnableFooterAlways = isEnableAlways;
    }

    /**
     * extends your HeaderViewHolder
     **/
    public abstract class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * extends your FooterViewHolder
     **/
    public abstract class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * extends your ItemViewHolder
     **/
    public abstract class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
