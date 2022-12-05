package com.buffersolve.news.repository

import com.buffersolve.news.api.RetrofitInstance
import com.buffersolve.news.db.ArticleDatabase
import com.buffersolve.news.models.Article

class NewsRepository(
    val db: ArticleDatabase
) {
    // Coroutine fun GET from network (retrofit2)
    suspend fun getBreakingNews(domains: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(domains, pageNumber)

    // Coroutine fun Search from network (retrofit2)
    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchNews(searchQuery, pageNumber)

    //
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    //
    fun gatSavedNews() = db.getArticleDao().getAllArticles()

    // Delete
    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}