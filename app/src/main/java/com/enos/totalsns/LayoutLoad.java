package com.enos.totalsns;

import android.os.Handler;
import android.util.Log;


public class LayoutLoad {
    static public final int IMAGE = 1;
    static public final int BACK = 2;

    private boolean isNotTransition = false;
    private boolean isImgLoaded = false;
    private boolean isBackLoaded = false;

    private boolean isUseImg = false;
    private boolean isUseBack = false;

    private OnLoadLayoutListener listener = null;

    private long startedTime = 0;

    private final long MINIMUM_POSTPONE = 100;

    private Handler handler = new Handler();

    private Runnable runnable = this::checkAndCallbackIfLoaded;

    public LayoutLoad(boolean useImg, boolean useBack, OnLoadLayoutListener listener) {
        this.isUseImg = useImg;
        this.isUseBack = useBack;
        this.listener = listener;
        this.startedTime = System.currentTimeMillis();
    }

    public boolean isLayoutLoaded() {
        boolean isImgOk = !isUseImg || isImgLoaded;
        boolean isBackOk = !isUseBack || isBackLoaded;
        boolean result = !isNotTransition && (isImgOk && isBackOk);
        Log.i("layout", result + " load");
        return result;
    }

    public void setImgLoadAndCallbackIfLaoded() {
        isImgLoaded = true;
        checkAndCallbackIfLoaded();
    }

    public void setBackLoadAndCallbackIfLaoded() {
        isBackLoaded = true;
        checkAndCallbackIfLoaded();
    }

    public void setNotTransition() {
        isNotTransition = true;
    }

    private void checkAndCallbackIfLoaded() {
        if (isLayoutLoaded() && listener != null) {
            long current = System.currentTimeMillis();
            if ((current - startedTime) > MINIMUM_POSTPONE) {
                Log.i("layout", "time ellapsed" + (current - startedTime));
                listener.onLayoutLoaded();
                listener = null;
            } else {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, startedTime + MINIMUM_POSTPONE - current);
            }
        }
    }
}