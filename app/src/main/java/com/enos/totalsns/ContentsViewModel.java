package com.enos.totalsns;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.widget.Toast;

import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.util.AppExecutors;
import com.enos.totalsns.util.ConvertUtils;
import com.enos.totalsns.util.SingleLiveEvent;

import twitter4j.User;

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
            if (isPressed) setFalseAfterDelay();
        });
    }

    public void signOut() {
        mRepository.signOut();
    }

    public LiveData<User> getLoggedInUser() {
        return mRepository.getLoggedInUser();
    }

    public LiveData<Boolean> isSignOutFinished() {
        return mRepository.isSignOutFinished();
    }

    public void onBackPressed() {
        if (isBackPressed.getValue()) {
            isShouldQuit.postValue(true);
        } else {
            Toast.makeText(
                    mContext,
                    mContext.getString(R.string.finish_message, ConvertUtils.getSecondsByMilli(Constants.QUIT_DELAY_MILLI)),
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
}
