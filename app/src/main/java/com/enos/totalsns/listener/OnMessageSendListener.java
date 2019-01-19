package com.enos.totalsns.listener;

import com.enos.totalsns.data.UserInfo;

public interface OnMessageSendListener {
    void onUserToSendSearchClicked(String query);

    void onUserToSendClicked(UserInfo item);
}
