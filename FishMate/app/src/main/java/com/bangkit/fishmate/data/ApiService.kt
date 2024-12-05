package com.bangkit.fishmate.data

import com.bangkit.fishmate.data.Response.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Endpoint untuk mencari berita berdasarkan query
    @GET("v2/everything")
    fun getNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String = "bf4374ec295e42a99952261bef02bbb9",
        @Query("language") language: String = "id",
        @Query("pageSize") pageSize: Int = 5
    ): Call<NewsResponse>
}