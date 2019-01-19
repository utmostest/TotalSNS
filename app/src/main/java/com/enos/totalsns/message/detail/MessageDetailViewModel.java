package com.enos.totalsns.message.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.QueryMessage;
import com.enos.totalsns.data.source.remote.QueryUploadMessage;

import java.io.File;
import java.util.List;

public class MessageDetailViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MediatorLiveData<Boolean> isNetworkOnUse;

    private MediatorLiveData<List<Message>> directMessageDetail;

    public MessageDetailViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        isNetworkOnUse = new MediatorLiveData<>();
        directMessageDetail = new MediatorLiveData<>();
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

    public LiveData<List<Message>> getDirectMessageDetail() {
        return directMessageDetail;
    }

    public void fetchDirectMessageDetail(long dmId) {
        mRepository.fetchDirectMessageDetail(dmId, directMessageDetail);
    }

    public void postDirectMessage(long receiverId, String message, File uploadFile, Message sample) {
        QueryUploadMessage query = new QueryUploadMessage(receiverId, message);
        query.setUploadingFile(uploadFile);
        mRepository.sendDirectMessage(query, sample);
    }

    public LiveData<Message> getCurrentUploadingDM() {
        return mRepository.getCurrentUploadingDM();
    }

    public UserInfo getUserFromCache(long id) {
        return mRepository.getUserFromCache(id);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        directMessageDetail.postValue(null);
        mRepository.getCurrentUploadingDM().postValue(null);
    }
}
