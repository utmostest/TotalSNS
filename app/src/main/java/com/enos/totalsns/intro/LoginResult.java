package com.enos.totalsns.intro;

import com.enos.totalsns.data.Account;

public class LoginResult {
    private boolean isLoginSucced;
    private int snsType;
    private String message;
    private Account account;

    public LoginResult() {

    }

    public LoginResult(boolean loginSucceed, int sns, String msg, Account acc) {
        isLoginSucced = loginSucceed;
        snsType = sns;
        message = msg;
        account = acc;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isLoginSucced() {
        return isLoginSucced;
    }

    public void setLoginSucced(boolean loginSucced) {
        isLoginSucced = loginSucced;
    }

    public int getSnsType() {
        return snsType;
    }

    public void setSnsType(int snsType) {
        this.snsType = snsType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
