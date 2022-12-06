package com.buffersolve.news.repository

import com.buffersolve.news.api.RetrofitInstance
import com.buffersolve.news.db.ArticleDatabase
import com.buffersolve.news.models.Article

class NewsRepository(
    val db: ArticleDatabase
) {
    // Retrofit
    suspend fun getBreakingNews(domains: String, country: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(domains, pageNumber)

    suspend fun searchNews(domains: String, searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchNews(domains, searchQuery, pageNumber)

    // Room
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun gatSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}