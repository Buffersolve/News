package com.buffersolve.news.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
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

class NewsViewModel(
    val newsRepository: NewsRepository
) : ViewModel() {

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
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(domains = domains, country = COUNTRY,  pageNumber = breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun searchNews(domains: String, searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(domains, searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
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

}
