package com.enos.totalsns.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.QueryUserTimeline;

import java.util.List;

public class ProfileViewModel extends ViewModel {

    private Context mContext;
    private TotalSnsRepository mRepository;
    private MutableLiveData<UserInfo> userProfile;
    private MutableLiveData<List<Article>> userTimeline;

    public ProfileViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        userProfile = new MutableLiveData<>();
        userTimeline = new MutableLiveData<>();
    }

    public LiveData<Boolean> isNetworkOnUse() {
        return mRepository.isSnsNetworkOnUse();
    }

    public LiveData<UserInfo> getUserProfile() {
        return userProfile;
    }

    public void fetchProfile(long userId) {
        mRepository.fetchProfile(userId, userProfile);
    }

    public LiveData<List<Article>> getUserTimeline() {
        return userTimeline;
    }

    public void fetchUserTimelineFirst(long userId) {
        mRepository.fetchUserTimeline(new QueryUserTimeline(QueryUserTimeline.FIRST, userId), userTimeline);
    }

    public void fetchUserTimelinePast() {
        mRepository.fetchUserTimeline(new QueryUserTimeline(QueryUserTimeline.PAST), userTimeline);
    }

    public void fetchUserTimelineRecent() {
        mRepository.fetchUserTimeline(new QueryUserTimeline(QueryUserTimeline.RECENT), userTimeline);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        userProfile.postValue(null);
    }
}
