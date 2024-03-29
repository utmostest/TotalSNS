/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.enos.totalsns.accounts;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.intro.LoginResult;

import java.util.List;

public class AccountsViewModel extends ViewModel {

    private final MediatorLiveData<List<Account>> mObservableAccounts;

    private Context mContext;
    private TotalSnsRepository mRepository;

    private MutableLiveData<LoginResult> loginResultMutableLiveData;

    public AccountsViewModel(Context application, TotalSnsRepository repository) {

        mContext = application;
        mRepository = repository;
        mObservableAccounts = new MediatorLiveData<>();
        loginResultMutableLiveData = repository.getLoginResult();
        // set by default null, until we get data from the database.
        mObservableAccounts.setValue(null);

        mObservableAccounts.addSource(repository.getAccounts(), mObservableAccounts::setValue);
    }

    public LiveData<List<Account>> getAccounts() {
        return mObservableAccounts;
    }

    public LiveData<LoginResult> getLoginResult(Account account, boolean b) {
        mRepository.signInTwitterWithAccount(account, b);
        return loginResultMutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearViewModel();
    }

    private void clearViewModel() {
        mObservableAccounts.postValue(null);
    }
}
