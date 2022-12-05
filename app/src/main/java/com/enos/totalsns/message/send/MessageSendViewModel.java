package com.enos.totalsns.message.send;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.data.source.remote.QuerySearchUser;

import java.util.List;

public class MessageSendViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;

    private MediatorLiveData<List<UserInfo>> sendToList;
    private MutableLiveData<List<UserInfo>> followingList;
    private MutableLiveData<List<UserInfo>> searchedList;

    public MessageSendViewModel(Context application, TotalSnsRepository repository) {
        mContext = application;
        mRepository = repository;
        sendToList = new MediatorLiveData<>();
        followingList = new MutableLiveData<>();
        searchedList = new MutableLiveData<>();
        sendToList.addSource(followingList, list -> {
            sendToList.postValue(list);
        });
        sendToList.addSource(searchedList, list -> {
            sendToList.postValue(list);
        });
    }

    public LiveData<Boolean> isNetworkOnUse() {
        return mRepository.isSnsNetworkOnUse();
    }

    public LiveData<List<UserInfo>> getSendToList() {
        return sendToList;
    }

    public void fetchFirstFollowList(QueryFollow queryFollow) {
        queryFollow.setUserId(mRepository.getLoggedInUser().getValue().getLongUserId());
        queryFollow.setFollower(false);
        mRepository.fetchFollowList(queryFollow, followingList);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        followingList.postValue(null);
        searchedList.postValue(null);
        sendToList.postValue(null);
    }

    public void fetchUserSearchList(String query) {
        mRepository.fetchSearchUser(new QuerySearchUser(QuerySearchUser.FIRST, query), searchedList);
    }
}