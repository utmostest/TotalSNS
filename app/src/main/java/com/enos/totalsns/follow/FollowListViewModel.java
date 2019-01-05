package com.enos.totalsns.follow;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.TotalSnsRepository;

import java.util.List;

public class FollowListViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;

    public FollowListViewModel(Context application, TotalSnsRepository repository) {
        mContext = application;
        mRepository = repository;
    }

    public LiveData<Boolean> isNetworkOnUse() {
        return mRepository.isSnsNetworkOnUse();
    }

    public LiveData<List<UserInfo>> getUserFollowList() {
        return mRepository.getFollowList();
    }

    public void fetchFirstFollowList(QueryFollow queryFollow) {
        mRepository.fetchFirstFollowList(queryFollow);
    }

    public void fetchNextFollowList() {
        mRepository.fetchNextFollowList();
    }

    public void fetchPreviousFollowList() {
        mRepository.fetchPreviousFollowList();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        mRepository.getFollowList().postValue(null);
    }
}