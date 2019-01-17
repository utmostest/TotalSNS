package com.enos.totalsns.userlist;

import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ItemSearchUserBinding;
import com.enos.totalsns.databinding.ItemUserBinding;

public interface OnUserClickListener {
    void onUserItemClicked(ItemUserBinding binding, UserInfo item);

    void onSearchUserItemClicked(ItemSearchUserBinding binding, UserInfo item);

    void onFollowButtonClicked(UserInfo info);
}
