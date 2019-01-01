package com.enos.totalsns.timeline.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.source.TotalSnsRepository;

public class TimelineDetailViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;

    private LiveData<Article> article;

    public TimelineDetailViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
    }

    public LiveData<Article> getArticle(long id) {
//        article = mRepository.getArticle(id);
        return article;
    }
}
