package com.enos.totalsns.listener;

import com.enos.totalsns.data.UserInfo;

public interface OnFollowListener {
    void onFollowTextClicked(UserInfo user, boolean isFollower);
}
