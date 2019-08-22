package com.enos.totalsns.data.source.local;



import com.enos.totalsns.data.Article;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ArticleDao {
    @Query("SELECT * FROM article where tableUserId=:tableId  AND isMention = 0 ORDER by postedAt DESC")
    LiveData<List<Article>> loadArticles(long tableId);

    @Query("SELECT * FROM article where tableUserId=:tableId")
    List<Article> getArticles(long tableId);

    @Query("SELECT * FROM article where tableUserId=:tableId and userId=:userId")
    List<Article> getArticlesByUserId(long tableId, long userId);

    @Query("select * from article where tableUserId=:tableId and articleId = :articleId")
    LiveData<Article> loadArticleById(long tableId, long articleId);

    @Query("select * from article where articleId = :articleId and tableUserId=:tableId ")
    Article getArticleById(long tableId, long articleId);

    @Query("SELECT * FROM article where snsType =:sns and tableUserId=:tableId ")
    LiveData<List<Article>> loadArticlesBySns(long tableId, int sns);

    @Query("SELECT * FROM article where tableUserId=:tableId ORDER by postedAt DESC LIMIT 1")
    LiveData<Article> loadLastArticle(long tableId);

    @Query("SELECT * FROM article where tableUserId=:tableId AND isMention = 0 ORDER by postedAt DESC LIMIT 1")
    Article getLastArticle(long tableId);

    @Query("SELECT * FROM article where tableUserId=:tableId  AND isMention = 0 ORDER by postedAt ASC LIMIT 1")
    Article getFirstArticle(long tableId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticles(List<Article> articles);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticle(Article article);

    @Update
    int updateArticle(Article article);

    @Update
    int updateArticles(List<Article> articles);

    @Query("DELETE FROM article WHERE tableUserId= :tableId and articleId = :articleId")
    int deleteArticleById(long tableId, long articleId);

    @Query("DELETE FROM article where tableUserId= :tableId")
    void deleteArticles(long tableId);

    // related mention article
    @Query("SELECT * FROM article where tableUserId=:tableId AND isMention = 1 ORDER by postedAt DESC")
    LiveData<List<Article>> loadMentions(long tableId);

    @Query("SELECT * FROM article where tableUserId=:tableId AND isMention = 1 ORDER by postedAt DESC LIMIT 1")
    Article getLastMention(long tableId);

    @Query("SELECT * FROM article where tableUserId=:tableId  AND isMention = 1 ORDER by postedAt ASC LIMIT 1")
    Article getFirstMention(long tableId);
}
