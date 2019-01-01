package com.enos.totalsns.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.enos.totalsns.data.Constants;

public class SingletonToast {

    private static SingletonToast instance;

    private static Toast mToast;

    private static Context mContext;

    private SingletonToast(Context context) {
        mContext = context;
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    public static SingletonToast getInstance() throws NullPointerException {
        if (mToast == null || mContext == null)
            throw new NullPointerException(SingletonToast.class.getSimpleName() + " is not initialized.");

        return instance;
    }

    public static SingletonToast getInstance(Context context) {
        if (instance == null) {
            instance = new SingletonToast(context);
        }

        return getInstance();
    }

    public void showShort(String message) {
        show(message, Toast.LENGTH_SHORT);
    }

    public void showShort(String tag, String message) {
        show(tag, message, Toast.LENGTH_SHORT);
    }

    public void showLong(String message) {
        show(message, Toast.LENGTH_SHORT);
    }

    public void showLong(String tag, String message) {
        show(tag, message, Toast.LENGTH_SHORT);
    }

    public void show(String message) {
        show(message, Toast.LENGTH_SHORT);
    }

    public void show(String message, int length) {
        show(getClass().getSimpleName(), message, length);
    }

    public void show(String tag, String message, int length) {
        show(Log.INFO, tag, message, length);
    }

    public void show(int log, String tag, String message, int length) {
        if (!Constants.IS_DEBUG) return;
        if (isValid()) {
            if (isShown()) {
                cancel(log, tag, message);
            }
            mToast = Toast.makeText(mContext, "tag : " + tag + "\nmessage : " + message, length);
            mToast.show();
        } else {
            log(log, tag, message);
        }
    }

    private boolean isValid() {
        return mToast != null;
    }

    private boolean isShown() {
        return isValid() && mToast.getView() != null && mToast.getView().isShown();
    }

    public void cancel(int log, String tag, String message) {
        if (mToast != null)
            mToast.cancel();
        log(log, tag, "toast is canceled\n" + message);
    }

    public void log(String message) {
        log(getClass().getSimpleName(), message);
    }

    public void log(String tag, String message) {
        log(Log.DEBUG, tag, message);
    }

    public void log(int log, String tag, String message) {
        if (!Constants.IS_DEBUG) return;
        Log.println(log, tag, message);
    }
}