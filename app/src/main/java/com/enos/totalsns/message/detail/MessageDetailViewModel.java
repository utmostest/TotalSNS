package com.enos.totalsns.message.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.source.TotalSnsRepository;

import java.util.List;

public class MessageDetailViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MediatorLiveData<Boolean> isNetworkOnUse;

    public MessageDetailViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        isNetworkOnUse = new MediatorLiveData<>();
        isNetworkOnUse.addSource(mRepository.isSnsNetworkOnUse(), (onUse) -> isNetworkOnUse.postValue(onUse));
    }

    public LiveData<Boolean> isNetworkOnUse() {
        return isNetworkOnUse;
    }

    public void fetchRecentDirectMessage() {
        mRepository.fetchRecentDirectMessage();
    }

    public void fetchPastDirectMessage() {
        mRepository.fetchPastDirectMessage();
    }

    public LiveData<List<Message>> getDirectMessageDetail() {
        return mRepository.getDirectMessageDetail();
    }

    public void fetchDirectMessageDetail(long dmId) {
        mRepository.fetchDirectMessageDetail(dmId);
    }

    public void postDirectMessage(long receiverId, String message, Message sample) {
        mRepository.sendDirectMessage(receiverId, message, sample);
    }

    public LiveData<Message> getCurrentUploadingDM() {
        return mRepository.getCurrentUploadingDM();
    }
}
