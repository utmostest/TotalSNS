package com.enos.totalsns.search;

import com.enos.totalsns.data.UserInfo;

public interface OnUserClickListener {
    void onUserItemClicked(UserInfo item);
    void onFollowButtonClicked(UserInfo info);
}
