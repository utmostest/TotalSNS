package com.enos.totalsns.data.source;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.source.local.TotalSnsDatabase;

import java.util.List;

/**
 * Repository handling the work with products and comments.
 */
public class DataRepository {

    //TODO SNS Client Repository 생성 및 SNS별 기능 추가
    private static DataRepository sInstance;

    private final TotalSnsDatabase mDatabase;
    private MediatorLiveData<List<Account>> mObservableAccounts;

    private DataRepository(final TotalSnsDatabase database) {
        mDatabase = database;
        mObservableAccounts = new MediatorLiveData<>();

        mObservableAccounts.addSource(mDatabase.accountDao().loadAccounts(),
                accounts -> mObservableAccounts.postValue(accounts));
    }

    public static DataRepository getInstance(final TotalSnsDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public LiveData<List<Account>> getAccounts() {
        return mObservableAccounts;
    }

    public Account loadAccountSync(final long uid) {
        return mDatabase.accountDao().getAccountById(uid);
    }
}
