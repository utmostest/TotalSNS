package com.enos.totalsns.intro;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.util.AppExecutors;

public class IntroViewModel extends ViewModel {

    private MutableLiveData<LoginResult> loginResultList = new MutableLiveData<LoginResult>();

    private AppExecutors mAppExecutors = new AppExecutors();
    private TotalSnsRepository mRepository;
    private Context mContext;
    private MutableLiveData<LoginResult> loginResultMutableLiveData;

    public IntroViewModel(Context application, TotalSnsRepository repository) {
        mContext = application;
        mRepository = repository;
        loginResultMutableLiveData = mRepository.getLoginResult();

    }

    public LiveData<LoginResult> getLoginResult() {
        mRepository.signInTwitterWithSaved(false);
        return loginResultMutableLiveData;
    }
}
