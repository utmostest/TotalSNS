package com.enos.totalsns.interfaces;

import com.enos.totalsns.data.Account;

public interface OnTwitterLogin {
    void onLoginFailed(String message);

    void onLoginSucceed(Account account);
}
