package com.enos.totalsns.login;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.OauthToken;
import com.enos.totalsns.intro.LoginResult;

public class LoginViewModel extends ViewModel {
    private TotalSnsRepository mRepository;
    private Context mContext;
    private MutableLiveData<LoginResult> loginResultMutableLiveData;

    public LoginViewModel(Context application, TotalSnsRepository repository) {
        mContext = application;
        mRepository = repository;
        loginResultMutableLiveData = repository.getLoginResult();
    }

    public MutableLiveData<LoginResult> getLoginResult() {
        return loginResultMutableLiveData;
    }

    public void signInFirstStep() {
        mRepository.init();
    }

    public void signOut() {
        mRepository.signOut();
    }

    public void signInTwitterWithOauthToken(OauthToken oauthToken, boolean b) {
        mRepository.signInTwitterWithOauthToken(oauthToken, b);
    }
}
