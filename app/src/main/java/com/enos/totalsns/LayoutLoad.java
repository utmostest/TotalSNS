package com.enos.totalsns;

import android.os.Handler;
import android.util.Log;

import com.enos.totalsns.listener.OnLoadLayoutListener;


public class LayoutLoad {
    static public final int IMAGE = 1;
    static public final int BACK = 2;

    private boolean isImgLoaded = false;
    private boolean isBackLoaded = false;

    private boolean isUseImg = false;
    private boolean isUseBack = false;

    private boolean isCompleted = false;

    private OnLoadLayoutListener listener = null;

    private long startedTime = 0;

    // 화면 전환 오류를 해결하기 위해 지연을 최소 100ms로 설정한다
    private final long MINIMUM_POSTPONE = 100;

    private Handler handler = new Handler();

    private Runnable runnable = this::checkAndCallbackIfLoaded;

    public LayoutLoad(boolean useImg, boolean useBack, OnLoadLayoutListener listener) {
        this.startedTime = System.currentTimeMillis();
        this.isUseImg = useImg;
        this.isUseBack = useBack;
        this.listener = listener;
        this.isCompleted = false;
    }

    public boolean isLayoutLoaded() {
        boolean isImgOk = !isUseImg || isImgLoaded;
        boolean isBackOk = !isUseBack || isBackLoaded;
        return isImgOk && isBackOk;
    }

    public void setImgLoadAndCallbackIfLaoded() {
        isImgLoaded = true;
        checkAndCallbackIfLoaded();
    }

    public void setBackLoadAndCallbackIfLaoded() {
        isBackLoaded = true;
        checkAndCallbackIfLoaded();
    }

    public void setDontTransition() {
        isCompleted = true;
    }

    private void checkAndCallbackIfLoaded() {
        if (!isCompleted && isLayoutLoaded() && listener != null) {
            long current = System.currentTimeMillis();
            if ((current - startedTime) > MINIMUM_POSTPONE) {
                Log.i("layout", "time ellapsed" + (current - startedTime));
                listener.onLayoutLoaded();
                isCompleted = true;
            } else {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, startedTime + MINIMUM_POSTPONE - current);
            }
        }
    }
}