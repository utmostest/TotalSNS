package com.enos.totalsns.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.enos.totalsns.AppExecutors;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.data.source.local.AppDatabase;
import com.enos.totalsns.data.source.remote.OauthToken;
import com.enos.totalsns.data.source.remote.TwitterManager;
import com.enos.totalsns.interfaces.OnTimelineResult;
import com.enos.totalsns.interfaces.OnTwitterInitComplete;
import com.enos.totalsns.interfaces.OnTwitterLogin;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

public class SnsClientViewModel extends AndroidViewModel {

    private AppExecutors mAppExecutors;
    private AppDatabase mAppDatabase;

    // TODO 각종 트위터 기능 추가

    public SnsClientViewModel(@NonNull Application application) {
        super(application);
        mAppExecutors = new AppExecutors();
        mAppDatabase = AppDatabase.getInstance(application);
    }

    public void init(OnTwitterInitComplete authorization) {
        mAppExecutors.networkIO().execute(() -> {
            TwitterManager.getInstance().init();
            if (authorization != null) {
                mAppExecutors.mainThread().execute(() -> requestAuthorizationUrl(authorization));
            }
        });
    }

    public void requestAuthorizationUrl(OnTwitterInitComplete onTwitterAuthorization) {
        mAppExecutors.networkIO().execute(() -> {
            try {
                String url = TwitterManager.getInstance().getAuthorizationUrl();
                if (onTwitterAuthorization != null) {
                    mAppExecutors.mainThread().execute(() -> onTwitterAuthorization.onInitCompleted(url));
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    public void signInTwitterWithSaved(OnTwitterLogin login, boolean isEnableUpdate) {
        mAppExecutors.networkIO().execute(() -> {
            TwitterManager.getInstance().init();
            List<Account> accountList = mAppDatabase.accountDao().loadCurrentAccountsBySns(Constants.TWITTER);
            if (accountList != null && accountList.size() > 0) {
                Account current = accountList.get(0);
                OauthToken oauthToken = new OauthToken(current.getOauthKey(), current.getOauthSecret());
                oauthToken.setIsAccessToken(true);
                signInTwitterWithOauthToken(oauthToken, login, isEnableUpdate);
            } else {
                if (login != null)
                    mAppExecutors.mainThread().execute(() -> login.onLoginFailed("you don't have saved account"));
            }
        });
    }

    public void signInTwitterWithAccount(Account account, OnTwitterLogin login, boolean isEnableUpdate) {
        signInTwitterWithOauthToken(new OauthToken(account), login, isEnableUpdate);
    }

    public void signInTwitterWithOauthToken(OauthToken token, OnTwitterLogin login, boolean isEnableUpdate) {
        mAppExecutors.networkIO().execute(() -> {
            try {
                if (token == null) {
                    mAppExecutors.mainThread().execute(() -> login.onLoginFailed("oauth isn't valid"));
                    return;
                }

                Account account = TwitterManager.getInstance().signInWithOauthToken(token);
                if (isEnableUpdate) {
                    mAppExecutors.diskIO().execute(() -> mAppDatabase.updateCurrentUser(account, Constants.TWITTER));
                }
                mAppExecutors.mainThread().execute(() -> login.onLoginSucceed(account));
            } catch (TwitterException e) {
                mAppExecutors.mainThread().execute(() -> login.onLoginFailed(e.getMessage()));
                e.printStackTrace();
            }
        });
    }

    public void signOut() {
        mAppExecutors.networkIO().execute(() -> {
            List<Account> accounts = mAppDatabase.accountDao().loadCurrentAccounts();
            if (accounts != null) {
                for (Account account : accounts) {
                    account.setCurrent(false);
                }
                mAppDatabase.accountDao().updateUsers(accounts);
            }
            TwitterManager.getInstance().signOut();
        });
    }

    public void getHomeTimeline(OnTimelineResult timeline) {
        mAppExecutors.networkIO().execute(() -> {
            try {
                ResponseList<Status> list = TwitterManager.getInstance().getTimeLine(new Paging(1, 30));
                List<Article> articleList = new ArrayList<Article>();
                for (Status status : list) {
                    User user = status.getUser();
                    Article article = new Article(user.getScreenName(), user.getName(), status.getText(), user.get400x400ProfileImageURL(), status.getCreatedAt().getTime());
                    articleList.add(article);
                }
                if (timeline != null) {
                    mAppExecutors.mainThread().execute(() -> timeline.onReceivedTimeline(articleList));
                }
            } catch (TwitterException e) {
                if (timeline != null) {
                    mAppExecutors.mainThread().execute(() -> timeline.onFailedReceiveTimeline(e.getMessage()));
                }
                e.printStackTrace();
            }
        });
    }
}
