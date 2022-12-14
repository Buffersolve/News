package com.buffersolve.news.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.buffersolve.news.getOrAwaitValue
import com.buffersolve.news.models.Article
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ArticleDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

   private lateinit var database: ArticleDatabase
   private lateinit var dao: ArticleDao

   @Before
   fun setup() {
       database = Room.inMemoryDatabaseBuilder(
           ApplicationProvider.getApplicationContext(),
           ArticleDatabase::class.java
       ).allowMainThreadQueries().build()
       dao = database.getArticleDao()
   }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun upsertAndGetArticlesTest() = runTest {
        val article = Article(1, "author", "content", "description",
        "publishedAt", "title", "https://www.google.com.ua/", "urlToImage")
        dao.upsert(article)

        val allArticle = dao.getAllArticles().getOrAwaitValue()

        assert(allArticle.contains(article))
    }

    @Test
    fun deleteShoppingItemTest() = runTest {
        val article = Article(1, "author", "content", "description",
            "publishedAt", "title", "https://www.google.com.ua/", "urlToImage")
        dao.upsert(article)
        dao.deleteArticle(article.url)

        val allArticle = dao.getAllArticles().getOrAwaitValue()

        assert(allArticle.isEmpty())
    }

    @Test
    fun isArtAlreadySavedTest() = runTest {
        val article = Article(1, "author", "content", "description",
            "publishedAt", "title", "https://www.google.com.ua/", "urlToImage")
        dao.upsert(article)

        val isAlreadySaved = dao.isArtAlreadySaved(article.url).getOrAwaitValue()

        assert(isAlreadySaved > 0)
    }

}