package com.enos.totalsns.message.send;

import com.enos.totalsns.data.UserInfo;

interface OnUserToSendClickListener {
    void onUserToSendClicked(UserInfo userInfo);

    void onUserToSendSearchClicked(String query);
}
