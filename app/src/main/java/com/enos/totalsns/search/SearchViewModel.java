package com.enos.totalsns.search;

import android.content.Context;

import androidx.collection.LongSparseArray;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.QuerySearchArticle;

import java.util.List;

import twitter4j.Query;

public class SearchViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MediatorLiveData<Boolean> isNetworkOnUse;
    private MutableLiveData<UserInfo> userProfile;

    public SearchViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        isNetworkOnUse = new MediatorLiveData<>();
        userProfile = new MutableLiveData<>();
        isNetworkOnUse.addSource(mRepository.isSnsNetworkOnUse(), (onUse) -> isNetworkOnUse.postValue(onUse));
    }

    public LiveData<Boolean> isNetworkOnUse() {
        return isNetworkOnUse;
    }

    public MutableLiveData<List<Article>> getSearchList() {
        return mRepository.getSearchList();
    }

    public void fetchSearch(String query) {
        getSearchList().setValue(null);
        getSearchUserList().setValue(null);
        mRepository.fetchSearchTotal(new Query(query).count(Constants.PAGE_CNT));
    }

    public void fetchPast() {
        mRepository.fetchSearch(new QuerySearchArticle(QuerySearchArticle.PAST));
    }

    public void fetchRecent() {
        mRepository.fetchSearch(new QuerySearchArticle(QuerySearchArticle.RECENT));
    }

    public LiveData<String> getSearchQuery() {
        return mRepository.getSearchQuery();
    }

    public MutableLiveData<List<UserInfo>> getSearchUserList() {
        return mRepository.getSearchUserList();
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

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        mRepository.getSearchList().postValue(null);
        mRepository.getSearchUserList().postValue(null);
    }
}
