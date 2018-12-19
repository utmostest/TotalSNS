package com.enos.totalsns.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.enos.totalsns.BuildConfig;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class TwitterUtil {

    private static TwitterUtil mTwitterUtil = null;

    private TwitterUtil() {

    }

    public static TwitterUtil getInstance() {
        if (mTwitterUtil == null) {
            mTwitterUtil = new TwitterUtil();
        }
        return mTwitterUtil;
    }

    public void init(@NonNull Context context, @NonNull String consumerKey, @NonNull String consumerSecret) {
        TwitterConfig config = new TwitterConfig.Builder(context.getApplicationContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(consumerKey, consumerSecret))
                .debug(BuildConfig.DEBUG)
                .build();
        Twitter.initialize(config);

        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        final OkHttpClient customClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor).build();

        final TwitterSession activeSession = TwitterCore.getInstance()
                .getSessionManager().getActiveSession();

        final TwitterApiClient customApiClient;
        if (activeSession != null) {
            customApiClient = new TwitterApiClient(activeSession, customClient);
            TwitterCore.getInstance().addApiClient(activeSession, customApiClient);
        } else {
            customApiClient = new TwitterApiClient(customClient);
            TwitterCore.getInstance().addGuestApiClient(customApiClient);
        }
    }
}
