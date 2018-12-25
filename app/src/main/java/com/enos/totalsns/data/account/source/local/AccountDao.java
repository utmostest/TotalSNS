package com.enos.totalsns.data.account.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.enos.totalsns.data.account.Account;

import java.util.List;

@Dao
public interface AccountDao {
    @Query("SELECT * FROM account")
    LiveData<List<Account>> loadAllAccounts();

    //1 is true
    @Query("SELECT * FROM account where isCurrent = 1")
    List<Account> loadCurrentAccounts();

    @Query("SELECT * FROM account where snsType =:sns")
    LiveData<List<Account>> loadAccountsBySns(int sns);

    //1 is true
    @Query("SELECT * FROM account where isCurrent = 1 and snsType=:sns")
    List<Account> loadCurrentAccountsBySns(int sns);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Account> accounts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Account account);

    @Update
    int updateUser(Account account);

    @Update
    int updateUsers(List<Account> accounts);

    @Query("select * from account where id = :accountId")
    LiveData<Account> loadAccounts(long accountId);

    @Query("select * from account where id = :accountId")
    Account loadAccountsSync(long accountId);
}
