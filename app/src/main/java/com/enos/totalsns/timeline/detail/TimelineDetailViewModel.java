package com.enos.totalsns.timeline.detail;

import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.source.TotalSnsRepository;

public class TimelineDetailViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;

    public TimelineDetailViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
    }
}
