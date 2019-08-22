package com.enos.totalsns.userlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;
import androidx.collection.LongSparseArray;

import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.data.source.remote.QuerySearchUser;
import com.enos.totalsns.util.SingletonToast;

import java.util.List;

public class UserListViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;

    private MutableLiveData<List<UserInfo>> userList;
    private MediatorLiveData<List<UserInfo>> searchedUserList;
    private MutableLiveData<UserInfo> userProfile;

    public UserListViewModel(Context application, TotalSnsRepository repository) {
        mContext = application;
        mRepository = repository;
        userList = new MutableLiveData<>();
        searchedUserList = new MediatorLiveData<>();
        userProfile = new MutableLiveData<>();
        SingletonToast.getInstance().log("instance_id", this.hashCode() + "");
    }

    public LiveData<Boolean> isNetworkOnUse() {
        return mRepository.isSnsNetworkOnUse();
    }

    public LiveData<List<UserInfo>> getUserFollowList() {
        return userList;
    }

    public void fetchFirstFollowList(QueryFollow queryFollow) {
        mRepository.fetchFollowList(queryFollow, userList);
    }

    public void fetchNextFollowList() {
        mRepository.fetchFollowList(new QueryFollow(QueryFollow.NEXT), userList);
    }

    public void fetchPreviousFollowList() {
        mRepository.fetchFollowList(new QueryFollow(QueryFollow.PREVIOUS), userList);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        userList.postValue(null);
        searchedUserList.postValue(null);
    }

    public LiveData<List<UserInfo>> getSearchedUserList() {
        return searchedUserList;
    }

    public void fetchFirstSearchedUserList() {
        mRepository.addSearchUserMoreSource(searchedUserList);
        mRepository.fetchSearchUserMore(new QuerySearchUser(QuerySearchUser.NEXT), searchedUserList);
    }

    public void fetchNextSearchedUserList() {
        mRepository.fetchSearchUserMore(new QuerySearchUser(QuerySearchUser.NEXT), searchedUserList);
    }

    public LiveData<UserInfo> getFollowUser() {
        return userProfile;
    }

    public void fetchFollow(long id, boolean isFollow) {
        mRepository.fetchFollow(id, isFollow, userProfile);
    }

    public LiveData<LongSparseArray<UserInfo>> getUserCache() {
        return mRepository.getUserCache();
    }
}