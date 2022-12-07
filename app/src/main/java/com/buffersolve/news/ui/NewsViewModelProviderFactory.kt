package com.buffersolve.news.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buffersolve.news.repository.NewsRepository

@Suppress("UNCHECKED_CAST")
class NewsViewModelProviderFactory(
    val app: Application,
    private val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app, newsRepository = newsRepository) as T
    }

}