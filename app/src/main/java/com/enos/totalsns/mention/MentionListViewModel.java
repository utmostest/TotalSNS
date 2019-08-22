package com.enos.totalsns.mention;

import android.content.Context;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.QueryMention;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

public class MentionListViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MediatorLiveData<Boolean> isNetworkOnUse;

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

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        mRepository.getMention().postValue(null);
    }
}
