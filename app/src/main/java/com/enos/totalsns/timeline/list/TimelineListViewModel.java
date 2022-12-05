package com.enos.totalsns.timeline.list;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.QueryTimeline;

import java.util.List;

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
        mRepository.fetchTimeline(new QueryTimeline(QueryTimeline.RECENT));
    }

    public void fetchPastTimeline() {
        mRepository.fetchTimeline(new QueryTimeline(QueryTimeline.PAST));
    }

    public void fetchTimelineForStart() {
        mRepository.fetchTimeline(new QueryTimeline(QueryTimeline.FIRST));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.getHomeTimeline().postValue(null);
    }
}
