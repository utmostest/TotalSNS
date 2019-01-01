package com.enos.totalsns.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.enos.totalsns.data.Mention;

import java.util.List;

@Dao
public interface MentionDao {
    @Query("SELECT * FROM mention where tableUserId=:tableId ORDER by postedAt DESC")
    LiveData<List<Mention>> loadMentions(long tableId);

    @Query("SELECT * FROM mention where tableUserId=:tableId")
    List<Mention> getMentions(long tableId);

    @Query("SELECT * FROM mention where tableUserId=:tableId and userId=:userId")
    List<Mention> getMentionsByUserId(long tableId, long userId);

    @Query("select * from mention where tableUserId=:tableId and articleId = :mentionId")
    LiveData<Mention> loadMentionById(long tableId, long mentionId);

    @Query("select * from mention where articleId = :mentionId and tableUserId=:tableId ")
    Mention getMentionById(long tableId, long mentionId);

    @Query("SELECT * FROM mention where snsType =:sns and tableUserId=:tableId ")
    LiveData<List<Mention>> loadMentionsBySns(long tableId, int sns);

    @Query("SELECT * FROM mention where tableUserId=:tableId ORDER by postedAt DESC LIMIT 1")
    LiveData<Mention> loadLastMention(long tableId);

    @Query("SELECT * FROM mention where tableUserId=:tableId ORDER by postedAt DESC LIMIT 1")
    Mention getLastMention(long tableId);

    @Query("SELECT * FROM mention where tableUserId=:tableId ORDER by postedAt ASC LIMIT 1")
    Mention getFirstMention(long tableId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMentions(List<Mention> mentions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMention(Mention mention);

    @Update
    int updateMention(Mention mention);

    @Update
    int updateMentions(List<Mention> mentions);

    @Query("DELETE FROM mention WHERE tableUserId= :tableId and articleId = :mentionId")
    int deleteMentionById(long tableId, long mentionId);

    @Query("DELETE FROM mention where tableUserId= :tableId")
    void deleteMentions(long tableId);
}
