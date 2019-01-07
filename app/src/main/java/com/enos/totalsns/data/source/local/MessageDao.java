package com.enos.totalsns.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.enos.totalsns.data.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT *, max(createdAt) AS maxCreatedAt FROM message where tableUserId=:tableId GROUP by senderTableId ORDER by createdAt DESC")
    LiveData<List<Message>> loadMessageList(long tableId);

    @Query("SELECT * FROM message where tableUserId=:tableId AND senderTableId=:sTableId ORDER by createdAt ASC")
    LiveData<List<Message>> loadMessagesBySenderId(long tableId, long sTableId);

    @Query("SELECT * FROM message where tableUserId=:tableId")
    List<Message> getMessages(long tableId);

    @Query("SELECT * FROM message where tableUserId=:tableId and receiverId=:userId")
    List<Message> getMessagesByUserId(long tableId, long userId);

    @Query("select * from message where tableUserId=:tableId and messageId = :messageId")
    LiveData<Message> loadMessageById(long tableId, long messageId);

    @Query("select * from message where messageId = :messageId and tableUserId=:tableId ")
    Message getMessageById(long tableId, long messageId);

    @Query("SELECT * FROM message where snsType =:sns and tableUserId=:tableId ")
    LiveData<List<Message>> loadMessagesBySns(long tableId, int sns);

    @Query("SELECT * FROM message where tableUserId=:tableId ORDER by createdAt DESC LIMIT 1")
    LiveData<Message> loadLastMessage(long tableId);

    @Query("SELECT * FROM message where tableUserId=:tableId ORDER by createdAt DESC LIMIT 1")
    Message getLastMessage(long tableId);

    @Query("SELECT * FROM message where tableUserId=:tableId ORDER by createdAt ASC LIMIT 1")
    Message getFirstMessage(long tableId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessages(List<Message> messages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(Message message);

    @Update
    int updateMessage(Message message);

    @Update
    int updateMessages(List<Message> messages);

    @Query("DELETE FROM message WHERE tableUserId= :tableId and messageId = :messageId")
    int deleteMessageById(long tableId, long messageId);

    @Query("DELETE FROM message where tableUserId= :tableId")
    void deleteMessages(long tableId);
}
