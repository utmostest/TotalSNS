package com.enos.totalsns.userlist;

import com.enos.totalsns.data.UserInfo;

public interface OnFollowListener {
    void onFollowTextClicked(UserInfo user, boolean isFollower);
}
