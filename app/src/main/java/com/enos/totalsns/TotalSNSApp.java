package com.enos.totalsns;

import androidx.multidex.MultiDexApplication;

import com.enos.totalsns.util.SingletonToast;

public class TotalSNSApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        SingletonToast.getInstance(this);
    }
}
