package com.enos.totalsns.search;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Search;
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

    public LiveData<List<Search>> getSearchList() {
        return mRepository.getSearchList();
    }

    public void fetchRecentSearch() {
        mRepository.fetchRecentSearch();
    }

    public void fetchPastSearch() {
        mRepository.fetchPastSearch();
    }

    public void fetchSearch(String query) {
        mRepository.fetchSearch(new Query(query).count(Constants.PAGE_CNT));
    }

    public void fetchSearchForStart() {
        fetchSearch(null);
    }

    public LiveData<String> getSearchQuery() {
        return mRepository.getSearchQuery();
    }
}
