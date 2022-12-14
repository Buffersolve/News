package com.buffersolve.news.repository

import com.buffersolve.news.api.RetrofitInstance
import com.buffersolve.news.db.ArticleDatabase
import com.buffersolve.news.models.Article

open class NewsRepository(
    private val db: ArticleDatabase
) {
    // Retrofit
    suspend fun getBreakingNews(domains: String) =
        RetrofitInstance.api.getBreakingNews(domains)

    suspend fun searchNews(domains: String, searchQuery: String) =
        RetrofitInstance.api.searchNews(domains, searchQuery)

    // Room
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun gatSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(articleUrl: String) = db.getArticleDao().deleteArticle(articleUrl)

    fun isArtAlreadySaved(articleUrl: String) = db.getArticleDao().isArtAlreadySaved(articleUrl)

}