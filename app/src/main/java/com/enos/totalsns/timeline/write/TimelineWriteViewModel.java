package com.enos.totalsns.timeline.write;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.source.TotalSnsRepository;

import java.util.concurrent.atomic.AtomicBoolean;

import twitter4j.User;

public class TimelineWriteViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MutableLiveData<AtomicBoolean> isShouldClose;

    public TimelineWriteViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        isShouldClose = new MutableLiveData<>();
    }

    public MutableLiveData<AtomicBoolean> isShouldClose() {
        return isShouldClose;
    }

    public void postArticle(Article article) {
        mRepository.uploadStatus(article.getMessage());
    }

    public void testSignIn() {
        mRepository.signInTwitterWithSaved(false);
    }

    public LiveData<Article> getUploadingArticle() {
        return mRepository.getCurrentUploadingArticle();
    }

    public LiveData<User> getCurrentUser() {
        return mRepository.getLoggedInUser();
    }
}
