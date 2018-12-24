package com.enos.totalsns;

import android.app.Application;

import com.enos.totalsns.data.source.DataRepository;
import com.enos.totalsns.data.source.local.AppDatabase;

public class TotalSNSApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }
}
