package com.enos.totalsns.message.list;

import android.content.Context;

import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.QueryMessage;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

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

    public void fetchRecentDirectMessage() {
        mRepository.fetchDirectMessage(new QueryMessage(QueryMessage.FIRST));
    }

    public void fetchPastDirectMessage() {
        mRepository.fetchDirectMessage(new QueryMessage(QueryMessage.NEXT));
    }

    public LiveData<List<Message>> getMessageList() {
        return mRepository.getDirectMessage();
    }

    public void fetchDirectMessage() {
        mRepository.fetchDirectMessage(new QueryMessage(QueryMessage.FIRST));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        mRepository.getDirectMessage().postValue(null);
    }
}
