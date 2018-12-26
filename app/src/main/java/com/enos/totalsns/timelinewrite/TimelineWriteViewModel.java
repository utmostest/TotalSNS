package com.enos.totalsns.timelinewrite;

import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.source.TotalSnsRepository;

public class TimelineWriteViewModel extends ViewModel {
    private Context mContext;
    private TotalSnsRepository mRepository;

    public TimelineWriteViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
    }
}
