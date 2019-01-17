package com.enos.totalsns;

public class LayoutLoad {
    static public final int IMAGE = 1;
    static public final int BACK = 2;

    private boolean isNotTransition = false;
    private boolean isImgLoaded = false;
    private boolean isBackLoaded = false;

    private boolean isUseImg = false;
    private boolean isUseBack = false;

    private OnLoadLayoutListener listener = null;

    public LayoutLoad(boolean useImg, boolean useBack, OnLoadLayoutListener listener) {
        this.isUseImg = useImg;
        this.isUseBack = useBack;
        this.listener = listener;
    }

    public boolean isLayoutLoaded() {
        boolean isImgOk = !isUseImg || isImgLoaded;
        boolean isBackOk = !isUseBack || isBackLoaded;
        return isNotTransition || (isImgOk && isBackOk);
    }

    public void setImgLoadAndCallbackIfLaoded() {
        isImgLoaded = true;
        checkAndCallbackIfLoaded();
    }

    public void setBackLoadAndCallbackIfLaoded() {
        isBackLoaded = true;
        checkAndCallbackIfLoaded();
    }

    public void setNotTransitionAndCallbackIfLaoded() {
        isNotTransition = true;
        checkAndCallbackIfLoaded();
    }

    private void checkAndCallbackIfLoaded() {
        if (isLayoutLoaded() && listener != null) listener.onLayoutLoaded();
    }
}