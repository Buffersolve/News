package com.buffersolve.news.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.buffersolve.news.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Query("DELETE FROM articles WHERE url = :articleUrl")
    suspend fun deleteArticle(articleUrl: String)

    @Query("SELECT COUNT(*) FROM articles WHERE url = :artUrl")
    fun isArtAlreadySaved(artUrl: String) : LiveData<Long>
}