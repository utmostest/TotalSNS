package com.enos.totalsns;

import android.app.Application;

import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.local.TotalSnsDatabase;
import com.enos.totalsns.util.SingletonToast;

public class TotalSNSApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SingletonToast.getInstance(this);
    }
}
