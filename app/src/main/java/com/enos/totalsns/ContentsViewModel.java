package com.enos.totalsns;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.source.TotalSnsRepository;

import java.util.List;

import twitter4j.Paging;
import twitter4j.User;

public class ContentsViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MediatorLiveData<Boolean> isNetworkOnUse;

    public ContentsViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        isNetworkOnUse = new MediatorLiveData<>();
        isNetworkOnUse.addSource(mRepository.isSnsNetworkOnUse(), (onUse) -> isNetworkOnUse.postValue(onUse));
    }

    public void signOut() {
        mRepository.signOut();
    }

    public LiveData<List<Article>> getHomeTimeline() {
        return mRepository.getHomeTimeline();
    }

    public LiveData<Boolean> isNetworkOnUse() {
        return isNetworkOnUse;
    }

    public void fetchRecentTimeline() {
        mRepository.fetchRecentTimeline();
    }

    public void fetchPastTimeline() {
        mRepository.fetchPastTimeline();
    }

    public LiveData<User> getLoggedInUser() {
        return mRepository.getLoggedInUser();
    }

    public LiveData<Boolean> isSignOutFinished() {
        return mRepository.isSignOutFinished();
    }
}
