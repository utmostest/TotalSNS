package com.enos.totalsns.timeline.write;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.TotalSnsRepository;

import java.util.concurrent.atomic.AtomicBoolean;

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
        mRepository.postArticle(article.getMessage());
    }

    public LiveData<Article> getUploadingArticle() {
        return mRepository.getCurrentUploadingArticle();
    }

    public LiveData<UserInfo> getCurrentUser() {
        return mRepository.getLoggedInUser();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        isShouldClose.postValue(null);
        mRepository.getCurrentUploadingArticle().postValue(null);
    }
}
