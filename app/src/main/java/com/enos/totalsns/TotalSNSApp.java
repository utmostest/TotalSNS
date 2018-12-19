package com.enos.totalsns;

import android.app.Application;

import com.enos.totalsns.util.TwitterUtil;

public class TotalSNSApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        TwitterUtil.getInstance().init(this,BuildConfig.CONSUMER_KEY,BuildConfig.CONSUMER_SECRET);
    }
}
