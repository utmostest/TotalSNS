package com.enos.totalsns.message.send;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.data.source.remote.QuerySearchUser;

import java.util.List;

public class MessageSendViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;

    public MessageSendViewModel(Context application, TotalSnsRepository repository) {
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
        queryFollow.setUserId(mRepository.getLoggedInUser().getValue().getLongUserId());
        queryFollow.setFollower(false);
        mRepository.fetchFirstFollowList(queryFollow);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        mRepository.getFollowList().postValue(null);
    }

    public LiveData<List<UserInfo>> getUserSearchList() {
        return mRepository.getSearchUserList();
    }

    public void fetchUserSearchList(String query) {
        mRepository.fetchSearchUser(new QuerySearchUser(QuerySearchUser.FIRST, query));
    }
}