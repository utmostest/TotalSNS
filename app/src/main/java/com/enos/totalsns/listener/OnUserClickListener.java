package com.enos.totalsns.listener;

import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemUserBinding;

public interface OnUserClickListener {
    void onUserItemClicked(ItemUserBinding binding, UserInfo item);
}