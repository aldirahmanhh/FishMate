package com.bangkit.fishmate.data

import com.bangkit.fishmate.data.Response.ApiResponse
import com.bangkit.fishmate.data.Response.LoginRequest
import com.bangkit.fishmate.data.Response.LoginResponse
import com.bangkit.fishmate.data.Response.NewsResponse
import com.bangkit.fishmate.data.Response.RegisterRequest
import com.bangkit.fishmate.data.Response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    //Login
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    //Register
    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

}