package com.enos.totalsns.intro;

import com.enos.totalsns.data.Account;

public class LoginResult {

    public static final int STEP1_INIT = 101;
    public static final int STEP2_AUTHORIZATION = 102;
    public static final int STEP3_ENTIRELOGIN = 103;

    public static final int STATUS_LOGIN_SUCCEED = 201;
    public static final int STATUS_LOGIN_CANCELED = 202;
    public static final int STATUS_LOGIN_FAILED = 203;

    private int loginStep;
    private int loginStatus;
    private int snsType;
    private String message;
    private Account account;
    private String authorizationUrl;

    private String token;
    private String tokenSecret;

    public LoginResult() {

    }

    public LoginResult(int status, int sns, String msg, Account acc) {
        loginStatus = status;
        snsType = sns;
        message = msg;
        account = acc;
    }

    public int getLoginStep() {
        return loginStep;
    }

    public void setLoginStep(int loginStep) {
        this.loginStep = loginStep;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }


    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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
