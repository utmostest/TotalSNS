package com.enos.totalsns.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.enos.totalsns.data.Article;

import java.util.List;

@Dao
public interface ArticleDao {
    @Query("SELECT * FROM article")
    LiveData<List<Article>> loadArticles();

    @Query("SELECT * FROM article")
    List<Article> getArticles();

    @Query("select * from article where articleId = :articleId")
    LiveData<Article> loadArticleById(long articleId);

    @Query("select * from article where articleId = :articleId")
    Article getArticleById(long articleId);

    @Query("SELECT * FROM article where snsType =:sns")
    LiveData<List<Article>> loadArticlesBySns(int sns);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticles(List<Article> articles);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticle(Article article);

    @Update
    int updateArticle(Article article);

    @Update
    int updateArticles(List<Article> articles);

    @Query("DELETE FROM article WHERE articleId = :articleId")
    int deleteArticleById(long articleId);

    @Query("DELETE FROM article")
    void deleteArticles();
}
