package com.enos.totalsns.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.TotalSnsRepository;

public class ProfileViewModel extends ViewModel {

    private Context mContext;
    private TotalSnsRepository mRepository;

    public ProfileViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
    }

    public LiveData<Boolean> isNetworkOnUse() {
        return mRepository.isSnsNetworkOnUse();
    }

    public LiveData<UserInfo> getUserProfile() {
        return mRepository.getUserProfile();
    }

    public void fetchProfile(long userId) {
        mRepository.fetchProfile(userId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel(){
        mRepository.getUserProfile().postValue(null);
    }
}
