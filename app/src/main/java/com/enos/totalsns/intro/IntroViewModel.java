package com.enos.totalsns.intro;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.enos.totalsns.custom.SingleLiveEvent;
import com.enos.totalsns.data.source.TotalSnsRepository;

public class IntroViewModel extends ViewModel {

    private TotalSnsRepository mRepository;
    private Context mContext;
    private SingleLiveEvent<LoginResult> loginResultMutableLiveData;

    public IntroViewModel(Context application, TotalSnsRepository repository) {
        mContext = application;
        mRepository = repository;
        loginResultMutableLiveData = mRepository.getLoginResult();
    }

    public LiveData<LoginResult> getLoginResult() {
        mRepository.signInTwitterWithSaved(false);
        return loginResultMutableLiveData;
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
