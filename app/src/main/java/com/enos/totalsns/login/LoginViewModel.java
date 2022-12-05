package com.enos.totalsns.login;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.enos.totalsns.custom.SingleLiveEvent;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.OauthToken;
import com.enos.totalsns.intro.LoginResult;

public class LoginViewModel extends ViewModel {
    private TotalSnsRepository mRepository;
    private Context mContext;
    private SingleLiveEvent<LoginResult> loginResultMutableLiveData;

    public LoginViewModel(Context application, TotalSnsRepository repository) {
        mContext = application;
        mRepository = repository;
        loginResultMutableLiveData = repository.getLoginResult();
    }

    public SingleLiveEvent<LoginResult> getLoginResult() {
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

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        loginResultMutableLiveData.postValue(null);
    }
}
