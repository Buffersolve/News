package com.buffersolve.news.api

import com.buffersolve.news.util.Constants.Companion.BASE_URL
import org.junit.Assert
import org.junit.Test

class RetrofitInstanceTest {


    @Test
    fun testRetrofitInstance() {

        val instance = RetrofitInstance.retrofit

        Assert.assertEquals(instance.baseUrl().toUrl().toString(), BASE_URL)

    }

}