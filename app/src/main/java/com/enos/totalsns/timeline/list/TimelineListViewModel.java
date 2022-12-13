package com.enos.totalsns.timeline.list;

import static com.enos.totalsns.data.Constants.INVALID_ID;

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
    private boolean isBetweenFetching = false;
    private Article currentBetween = null;

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

    public void fetchBetweenTimeline(Article article) {
        isBetweenFetching = true;
        currentBetween = article;
        QueryTimeline query = new QueryTimeline(QueryTimeline.BETWEEN);
        query.setMaxId(article.getArticleId() - 1);
        query.setSinceId(article.getSinceId());
        mRepository.fetchTimeline(query);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.getHomeTimeline().postValue(null);
    }

    public boolean isBetweenFetching() {
        return isBetweenFetching;
    }

    public void setBetweenFetching(boolean betweenFetching) {
        isBetweenFetching = betweenFetching;
        currentBetween.setSinceId(INVALID_ID);
        mRepository.updateArticle(currentBetween);
    }
}
