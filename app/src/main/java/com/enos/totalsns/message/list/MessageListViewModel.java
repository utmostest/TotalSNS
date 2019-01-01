package com.enos.totalsns.message.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.source.TotalSnsRepository;

import java.util.List;

import twitter4j.Paging;

public class MessageListViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MediatorLiveData<Boolean> isNetworkOnUse;

    public MessageListViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        isNetworkOnUse = new MediatorLiveData<>();
        isNetworkOnUse.addSource(mRepository.isSnsNetworkOnUse(), (onUse) -> isNetworkOnUse.postValue(onUse));
    }

    public LiveData<Boolean> isNetworkOnUse() {
        return isNetworkOnUse;
    }

    public void fetchRecentTimeline() {
        mRepository.fetchRecentDirectMessage();
    }

    public void fetchPastTimeline() {
        mRepository.fetchPastDirectMessage();
    }

    public LiveData<List<Message>> getMessageList() {
        return mRepository.getDirectMessage(5);
    }
}