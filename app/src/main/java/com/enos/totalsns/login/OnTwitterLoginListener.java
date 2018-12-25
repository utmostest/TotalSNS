package com.enos.totalsns.login;

import com.enos.totalsns.data.account.Account;

public interface OnTwitterLoginListener {
    void onLoginFailed(String message);

    void onLoginSucceed(Account account);
}
