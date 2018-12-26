package com.enos.totalsns;

import android.app.Application;

import com.enos.totalsns.data.source.DataRepository;
import com.enos.totalsns.data.source.local.TotalSnsDatabase;

public class TotalSNSApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

    }

    public TotalSnsDatabase getDatabase() {
        return TotalSnsDatabase.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }
}
