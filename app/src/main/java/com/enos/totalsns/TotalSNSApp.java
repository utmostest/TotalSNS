package com.enos.totalsns;

import android.app.Application;

import com.enos.totalsns.util.SingletonToast;

public class TotalSNSApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SingletonToast.getInstance(this);
    }
}
