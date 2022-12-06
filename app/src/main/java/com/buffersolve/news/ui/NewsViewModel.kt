package com.buffersolve.news.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.buffersolve.news.NewsApplication
import com.buffersolve.news.models.Article
import com.buffersolve.news.models.NewsResponse
import com.buffersolve.news.repository.NewsRepository
import com.buffersolve.news.util.Constants.Companion.COUNTRY
import com.buffersolve.news.util.Constants.Companion.DOMAINS
import com.buffersolve.news.util.Constants.Companion.ELEMENT_ATTR_MATCH
import com.buffersolve.news.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1

    val parsedText: MutableLiveData<String> = MutableLiveData()

    init {
        getBreakingNews(DOMAINS)
    }

    // Coroutine ViewModel Scopes
    fun getBreakingNews(domains: String) = viewModelScope.launch {
        safeBreakingNewsCall(domains)
    }

    fun searchNews(domains: String, searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(domains, searchQuery)
    }

    fun parseHTML(article: Article) = viewModelScope.launch (Dispatchers.IO) {
        parsedText.postValue(parse(article))
    }

    // Breaking Fragment Get function
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(message = response.message())
    }

    // Search Fragment Search function
    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(message = response.message())
    }

    // Parse
    private fun parse (article: Article): String {
            val doc: Document = Jsoup.connect(article.url).get()
            return doc.getElementsByAttributeValueContaining("class", ELEMENT_ATTR_MATCH).text()
//            return doc.getElementsByTag("p").text()
    }

    // Save Article
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    // Get Saved News
    fun getSavedNews() = newsRepository.gatSavedNews()

    // Delete Article
    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    // Save Call
    private suspend fun safeBreakingNewsCall (domains: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(domains = domains, country = COUNTRY,  pageNumber = breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error(message = "No internet connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> breakingNews.postValue(Resource.Error(message = "Network Failure"))
                else -> breakingNews.postValue(Resource.Error(message = "Conversion Error"))
            }
        }
    }

    // Search Save Call
    private suspend fun safeSearchNewsCall (domains: String, searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(domains = domains, searchQuery = searchQuery, pageNumber = searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error(message = "No internet connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error(message = "Network Failure"))
                else -> searchNews.postValue(Resource.Error(message = "Conversion Error"))
            }
        }
    }

    // Check Internet Connection
    private fun hasInternetConnection() : Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}
