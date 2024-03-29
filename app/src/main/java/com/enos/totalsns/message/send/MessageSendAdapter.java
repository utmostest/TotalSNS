package com.enos.totalsns.message.send;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.recyclerview.widget.DiffUtil;

import com.enos.totalsns.custom.HFSupportAdapter;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemMessageSendBinding;
import com.enos.totalsns.databinding.ItemMessageSendHeaderBinding;
import com.enos.totalsns.listener.OnMessageSendListener;
import com.enos.totalsns.util.CompareUtils;
import com.enos.totalsns.util.GlideUtils;

import java.util.List;

public class MessageSendAdapter extends HFSupportAdapter {

    // TODO SNS별 뷰홀더 추가 및 화면 표시
    private List<UserInfo> mValues;
    private OnMessageSendListener mListener;

    private final int TYPE_TWITTER = Constants.TWITTER;
    private final int TYPE_FACEBOOK = Constants.FACEBOOK;
    private final int TYPE_INSTAGRAM = Constants.INSTAGRAM;

    private int mSnsType = Constants.DEFAULT_SNS;

    private boolean isItemChanged = false;

    public MessageSendAdapter(List<UserInfo> list, OnMessageSendListener listener) {
        mValues = list;
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
    public HFSupportAdapter.HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        ItemMessageSendHeaderBinding itemHeaderBinding = ItemMessageSendHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HeaderViewHolder(itemHeaderBinding);
    }

    @Override
    public HFSupportAdapter.FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        ItemMessageSendHeaderBinding itemFooterBinding = ItemMessageSendHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FooterViewHolder(itemFooterBinding);
    }

    @Override
    public HFSupportAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        ItemMessageSendBinding itemBinding = ItemMessageSendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(itemBinding);
    }

    @Override
    public void onBindHeaderViewHolder(HFSupportAdapter.HeaderViewHolder vh, int pos) {
        HeaderViewHolder holder = (HeaderViewHolder) vh;
        holder.bind();
    }

    @Override
    public void onBindFooterViewHolder(HFSupportAdapter.FooterViewHolder vh, int pos) {
        FooterViewHolder holder = (FooterViewHolder) vh;
        holder.bind();
    }

    @Override
    public void onBindItemViewHolder(HFSupportAdapter.ItemViewHolder vh, int position) {
        ItemViewHolder holder = (ItemViewHolder) vh;
        holder.bind(mValues.get(position));
    }

    @Override
    public int getYourItemViewType(int position) {
        return 0;
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
        isItemChanged = true;
    }

    private class ItemViewHolder extends HFSupportAdapter.ItemViewHolder {
        public final ItemMessageSendBinding binding;

        ItemViewHolder(ItemMessageSendBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind(UserInfo mItem) {
            binding.msUserId.setText(mItem.getUserId());
            binding.msUserName.setText(mItem.getUserName());
            GlideUtils.loadProfileImage(binding.getRoot().getContext(), mItem.getProfileImg(), binding.msProfileImg);
            binding.getRoot().setOnClickListener(v -> {
                if (mListener != null) mListener.onUserToSendClicked(mItem);
            });
        }
    }

    private class HeaderViewHolder extends HFSupportAdapter.HeaderViewHolder {
        public final ItemMessageSendHeaderBinding binding;

        HeaderViewHolder(ItemMessageSendHeaderBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {
            binding.messageSendBtn.setOnClickListener(v -> {
                Editable editable = binding.messageSendEdit.getText();
                if (editable.toString().length() > 0) {
                    if (mListener != null) {
                        mListener.onUserToSendSearchClicked(editable.toString());
                        hideKeyboard();
                    }
                }
            });
            binding.messageSendEdit.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && v.getText().length() > 0) {
                    if (mListener != null) {
                        mListener.onUserToSendSearchClicked(v.getText().toString());
                        hideKeyboard();
                    }
                }
                return true;
            });
        }

        private void hideKeyboard() {
            InputMethodManager imm = (InputMethodManager) binding.getRoot().getContext().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.messageSendEdit.getWindowToken(), 0);
        }
    }

    private class FooterViewHolder extends HFSupportAdapter.FooterViewHolder {
        public final ItemMessageSendHeaderBinding binding;

        FooterViewHolder(ItemMessageSendHeaderBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind() {

        }
    }
}
