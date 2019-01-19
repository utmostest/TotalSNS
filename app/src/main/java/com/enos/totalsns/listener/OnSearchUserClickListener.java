package com.enos.totalsns.listener;

import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemSearchUserBinding;

public interface OnSearchUserClickListener {
    void onSearchUserItemClicked(ItemSearchUserBinding binding, UserInfo item);
}
