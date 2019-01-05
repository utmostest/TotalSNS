package com.enos.totalsns.nearby;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.remote.QueryArticleNearBy;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class NearbyArticleViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MediatorLiveData<Boolean> isNetworkOnUse;

    public NearbyArticleViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        isNetworkOnUse = new MediatorLiveData<>();
        isNetworkOnUse.addSource(mRepository.isSnsNetworkOnUse(), (onUse) -> isNetworkOnUse.postValue(onUse));
    }

    public LiveData<Boolean> isNetworkOnUse() {
        return isNetworkOnUse;
    }

    public LiveData<List<Article>> getNearbySearchList() {
        return mRepository.getNearbySearchList();
    }

    public void fetchNearbyFirst(LatLng latLng, double radius) {
        mRepository.getNearbySearchList().setValue(null);
        QueryArticleNearBy query = new QueryArticleNearBy(QueryArticleNearBy.FIRST, latLng.latitude, latLng.longitude);
        query.setRadius(radius);
        mRepository.fetchNearbySearch(query);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        mRepository.getNearbySearchList().postValue(null);
    }

    public void fetchNearbyPast() {
        QueryArticleNearBy query = new QueryArticleNearBy(QueryArticleNearBy.PAST);
        mRepository.fetchNearbySearch(query);
    }
}
