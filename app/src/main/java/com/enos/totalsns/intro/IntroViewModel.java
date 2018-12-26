package com.enos.totalsns.intro;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.source.local.TotalSnsDatabase;
import com.enos.totalsns.data.source.remote.OauthToken;
import com.enos.totalsns.data.source.remote.TwitterManager;
import com.enos.totalsns.util.AppExecutors;

import twitter4j.TwitterException;

public class IntroViewModel extends ViewModel {

    private MutableLiveData<LoginResult> loginResultList = new MutableLiveData<LoginResult>();

    private AppExecutors mAppExecutors = new AppExecutors();

    public IntroViewModel() {
    }

    public void attempCurrentAccoutsLogin(Context context) {
        mAppExecutors.networkIO().execute(() -> {
            TwitterManager.getInstance().init();
            Account current = TotalSnsDatabase.getInstance(context).accountDao().getCurrentAccountsBySns(Constants.TWITTER);
            if (current != null) {
                OauthToken oauthToken = new OauthToken(current.getOauthKey(), current.getOauthSecret());
                oauthToken.setIsAccessToken(true);
                signInTwitterWithOauthToken(oauthToken, current);
            } else {
                LoginResult result = new LoginResult(false, Constants.TWITTER, "you don't have signed in account", current);
                addLoginResultAndPost(result);
            }
        });
    }

    public void signInTwitterWithOauthToken(OauthToken token, Account current) {
        mAppExecutors.networkIO().execute(() -> {
            try {
                Account account = TwitterManager.getInstance().signInWithOauthToken(token);
                LoginResult result = new LoginResult(true, Constants.TWITTER, "login succeed", account);
                addLoginResultAndPost(result);
            } catch (TwitterException e) {
                LoginResult result = new LoginResult(false, Constants.TWITTER, e.getMessage(), current);
                addLoginResultAndPost(result);
                e.printStackTrace();
            }
        });
    }

    private void addLoginResultAndPost(LoginResult result) {
        getLoginResultList().postValue(result);
    }

    public MutableLiveData<LoginResult> getLoginResultList() {
        return loginResultList;
    }

    public void setLoginResultList(MutableLiveData<LoginResult> loginResultList) {
        this.loginResultList = loginResultList;
    }
}
