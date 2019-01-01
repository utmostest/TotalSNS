package com.enos.totalsns.timeline.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.source.TotalSnsRepository;

import java.util.List;

import twitter4j.Paging;

public class TimelineListViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MediatorLiveData<Boolean> isNetworkOnUse;

    public TimelineListViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        isNetworkOnUse = new MediatorLiveData<>();
        isNetworkOnUse.addSource(mRepository.isSnsNetworkOnUse(), (onUse) -> isNetworkOnUse.postValue(onUse));
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

    public void fetchTimelineForStart() {
        mRepository.fetchTimelineForStart(new Paging().count(Constants.PAGE_CNT));
    }
}
