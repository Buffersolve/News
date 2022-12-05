package com.buffersolve.news.api

import com.buffersolve.news.models.NewsResponse
import com.buffersolve.news.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("/v2/everything")
    suspend fun getBreakingNews(

        @Query("domains")
        domains: String,

//        @Query("country")
//        countryCode: String,

        @Query("page")
        pageNumber: Int = 1,

        @Query("apiKey")
        apiKey: String = API_KEY

    ) : Response<NewsResponse>

    @GET("/v2/everything")
    suspend fun searchNews(

        @Query("q")
        searchQuery: String,

        @Query("page")
        pageNumber: Int = 1,

        @Query("apiKey")
        apiKey: String = API_KEY

    ) : Response<NewsResponse>

}