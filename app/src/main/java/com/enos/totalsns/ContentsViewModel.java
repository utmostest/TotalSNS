package com.enos.totalsns;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.widget.Toast;

import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.custom.SingleLiveEvent;
import com.enos.totalsns.util.TimeUtils;

public class ContentsViewModel extends ViewModel {
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private TotalSnsRepository mRepository;
    private MediatorLiveData<Boolean> isShouldQuit;
    private MutableLiveData<Boolean> isBackPressed;

    public ContentsViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        isShouldQuit = new MediatorLiveData<>();
        isBackPressed = new MutableLiveData<>();
        isBackPressed.setValue(false);
        isShouldQuit.addSource(isBackPressed, (isPressed) -> {
            if (isPressed != null && isPressed) setFalseAfterDelay();
        });
    }

    public void signOut() {
        mRepository.signOut();
    }

    public LiveData<UserInfo> getLoggedInUser() {
        return mRepository.getLoggedInUser();
    }

    public LiveData<Boolean> isSignOutFinished() {
        return mRepository.isSignOutFinished();
    }

    public void onBackPressed() {
        if (isBackPressed.getValue() != null && isBackPressed.getValue()) {
            isShouldQuit.postValue(true);
        } else {
            Toast.makeText(
                    mContext,
                    mContext.getString(R.string.finish_message, TimeUtils.getSecondsByMilli(Constants.QUIT_DELAY_MILLI)),
                    Toast.LENGTH_SHORT).show();
            isBackPressed.postValue(true);
        }
    }

    private void setFalseAfterDelay() {
        AppExecutors appExecutors = new AppExecutors();
        appExecutors.diskIO().execute(() -> {
            try {
                Thread.sleep(Constants.QUIT_DELAY_MILLI);
                isBackPressed.postValue(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public LiveData<Boolean> sholudQuit() {
        return isShouldQuit;
    }

    public SingleLiveEvent<String> getSearchQuery() {
        return mRepository.getSearchQuery();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        isShouldQuit.postValue(null);
        mRepository.getSearchQuery().postValue(null);
    }

    public LiveData<LongSparseArray<UserInfo>> getUserCache() {
        return mRepository.getUserCache();
    }
}
