package com.bangkit.fishmate.data


import com.bangkit.fishmate.data.Response.ChangePasswordRequest
import com.bangkit.fishmate.data.Response.ChangePasswordResponse
import com.bangkit.fishmate.data.Response.DiagnosisResponse
import com.bangkit.fishmate.data.Response.FishBannerResponse
import com.bangkit.fishmate.data.Response.ForgotPasswordRequest
import com.bangkit.fishmate.data.Response.ForgotPasswordResponse
import com.bangkit.fishmate.data.Response.LoginRequest
import com.bangkit.fishmate.data.Response.LoginResponse
import com.bangkit.fishmate.data.Response.NewsResponse
import com.bangkit.fishmate.data.Response.ProductResponse
import com.bangkit.fishmate.data.Response.RegisterRequest
import com.bangkit.fishmate.data.Response.RegisterResponse
import com.bangkit.fishmate.data.Response.ResetPasswordRequest
import com.bangkit.fishmate.data.Response.ResetPasswordResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("v2/everything")
    fun getNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String = "bf4374ec295e42a99952261bef02bbb9",
        @Query("language") language: String = "id",
        @Query("pageSize") pageSize: Int = 5
    ): Call<NewsResponse>

    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("auth/changePassword")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Call<ChangePasswordResponse>

    @POST("auth/forgotPassword")
    fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Call<ForgotPasswordResponse>

    @POST("auth/resetPassword")
    fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Call<ResetPasswordResponse>

    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<DiagnosisResponse>

    @GET("search-v2")
    fun getProducts(
        @Query("q") query: String = "fish medication",
        @Query("country") country: String = "us",
        @Query("language") language: String = "en",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("sort_by") sortBy: String = "BEST_MATCH",
        @Query("product_condition") productCondition: String = "ANY"
    ): Response<ProductResponse>

    @GET("fish/getfish/{id}")
    fun getFishDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<FishBannerResponse>

}