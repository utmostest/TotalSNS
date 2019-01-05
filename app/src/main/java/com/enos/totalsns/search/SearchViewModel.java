package com.enos.totalsns.search;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.TotalSnsRepository;

import java.util.List;

import twitter4j.Query;

public class SearchViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MediatorLiveData<Boolean> isNetworkOnUse;

    public SearchViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        isNetworkOnUse = new MediatorLiveData<>();
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

    public LiveData<String> getSearchQuery() {
        return mRepository.getSearchQuery();
    }

    public MutableLiveData<List<UserInfo>> getSearchUserList() {
        return mRepository.getSearchUserList();
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
