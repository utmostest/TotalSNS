package com.enos.totalsns.data.source.local;


import com.enos.totalsns.data.Account;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AccountDao {
    @Query("SELECT * FROM account")
    LiveData<List<Account>> loadAccounts();

    @Query("SELECT * FROM account")
    List<Account> getAccounts();

    @Query("select * from account where id = :accountId")
    LiveData<Account> loadAccountById(long accountId);

    @Query("select * from account where id = :accountId")
    Account getAccountById(long accountId);

    //1 is true
    @Query("SELECT * FROM account where isCurrent = 1")
    List<Account> getCurrentAccounts();

    @Query("SELECT * FROM account where snsType =:sns")
    LiveData<List<Account>> loadAccountsBySns(int sns);

    //1 is true
    @Query("SELECT * FROM account where isCurrent = 1 and snsType=:sns")
    Account getCurrentAccountsBySns(int sns);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAccounts(List<Account> accounts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAccount(Account account);

    @Update
    int updateAccount(Account account);

    @Update
    int updateAccounts(List<Account> accounts);

    @Query("UPDATE account SET isCurrent = 0 WHERE isCurrent = 1")
    void updateSignOut();

    @Query("UPDATE account SET isCurrent = 0 WHERE snsType = :sns and isCurrent = 1")
    void updateSignOutBySns(int sns);


    @Query("DELETE FROM account WHERE id = :accountId")
    int deleteAccountById(long accountId);

    @Query("DELETE FROM account")
    void deleteAccounts();
}
