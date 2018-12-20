package com.enos.totalsns.util;

import android.content.Context;
import android.support.annotation.NonNull;

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

    }
}
