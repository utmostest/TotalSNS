package com.enos.totalsns.mention;

import static com.enos.totalsns.data.Constants.INVALID_ID;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.QueryMention;

import java.util.List;

public class MentionListViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MediatorLiveData<Boolean> isNetworkOnUse;
    private boolean isBetweenFetching = false;
    private Article currentBetween = null;

    public MentionListViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        isNetworkOnUse = new MediatorLiveData<>();
        isNetworkOnUse.addSource(mRepository.isSnsNetworkOnUse(), (onUse) -> isNetworkOnUse.postValue(onUse));
    }

    public LiveData<List<Article>> getMention() {
        return mRepository.getMention();
    }

    public LiveData<Boolean> isNetworkOnUse() {
        return isNetworkOnUse;
    }

    public void fetchRecentMention() {
        mRepository.fetchMention(new QueryMention(QueryMention.RECENT));
    }

    public void fetchPastMention() {
        mRepository.fetchMention(new QueryMention(QueryMention.PAST));
    }

    public void fetchMentionForStart() {
        mRepository.fetchMention(new QueryMention(QueryMention.FIRST));
    }

    public void fetchMentionForBetween(Article article) {
        isBetweenFetching = true;
        currentBetween = article;
        QueryMention query = new QueryMention(QueryMention.BETWEEN);
        query.setSinceId(article.getSinceId());
        query.setMaxId(article.getArticleId() - 1);
        mRepository.fetchMention(query);
    }

    public boolean isBetweenFetching() {
        return isBetweenFetching;
    }

    public void setBetweenFetching(boolean betweenFetching) {
        isBetweenFetching = betweenFetching;
        currentBetween.setSinceId(INVALID_ID);
        mRepository.updateArticle(currentBetween);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        mRepository.getMention().postValue(null);
    }

}
