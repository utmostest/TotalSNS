package com.enos.totalsns.follow;

import com.enos.totalsns.data.UserInfo;

public interface OnFollowListener {
    void onFollowClicked(UserInfo user, boolean isFollower);
}
