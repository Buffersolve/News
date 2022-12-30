package com.buffersolve.news.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.buffersolve.news.MainCoroutineRule
import com.buffersolve.news.NewsApplication
import com.buffersolve.news.repository.NewsRepository
import com.buffersolve.news.util.Constants.Companion.DOMAINS
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: NewsViewModel
    private lateinit var app: NewsApplication
    private lateinit var newsRepository: NewsRepository
    private val domains = DOMAINS

    @Before
    fun setUp() {
        app = mockk()
        newsRepository = mockk()
        viewModel = NewsViewModel(app, domains, newsRepository)
    }


    @Test
    fun getBreakingNewsTest() {
//        val app = mockk<NewsApplication>()
//        val newsRepository = mockk<NewsRepository>()
//        val domains = DOMAINS
//        val viewModel = NewsViewModel(app, DOMAINS, newsRepository)

//        val a = viewModel.getBreakingNews(domains).children


    }


}