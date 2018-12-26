package com.enos.totalsns.timelines;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.source.TotalSnsRepository;

import java.util.ArrayList;

import twitter4j.Paging;

public class TimelineViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;

    public TimelineViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
    }

    public void signOut() {
        mRepository.signOut();
    }

    public LiveData<ArrayList<Article>> getHomeTimeline() {
        mRepository.fetchTimeline(new Paging(1, 30));
        return mRepository.getHomeTimeline(new Paging(1, 30));
    }
}
