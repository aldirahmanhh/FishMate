package com.bangkit.fishmate.data.Response

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

data class Article(
    val source: Source,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?
)

data class Source(
    val id: String?,
    val name: String?
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val error: Boolean,
    val message: String,
    val data: RegisterData
)

data class RegisterData(
    val username: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)

data class LoginResult(
    val id: String,
    val name: String,
    val token: String
)

data class ChangePasswordResponse(
    val error: Boolean,
    val message: String
)

data class ChangePasswordRequest(
    val email: String,
    val password: String,
    val newPassword: String,
    val confirmPassword: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ForgotPasswordResponse(
    val error: Boolean,
    val message: String
)

data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val newPassword: String,
    val confirmPassword: String
)

data class ResetPasswordResponse(
    val error: Boolean,
    val message: String
)

data class ApiResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

data class Product(
    val product_id: String,
    val product_title: String,
    val product_description: String,
    val product_photos: List<String>,
    val product_rating: Double,
    val product_page_url: String,
    val product_reviews_page_url: String,
    val offer: ProductOffer
)

data class ProductOffer(
    val offerId: String,
    val offerPageUrl: String,
    val price: String,
    val shipping: String,
    val storeName: String,
    val storeRating: String,
    val storeReviewCount: Int,
    val storeReviewsPageUrl: String
)

data class ProductData(
    val products: List<Product>
)

data class ProductResponse(
    val status: String,
    val requestId: String,
    val data: ProductData
)

data class DiagnosisResponse(
    val confidence: Double,
    val diagnosis: Diagnosis,
    val filePath: String,
    val message: String,
    @SerializedName("model_output") val modelOutput: List<List<Double>>,
    val predictedClass: String
)

data class Diagnosis(
    val explanation: String,
    val id: Int,
    val label: String,
    val suggestion: String
)

data class DetectionHistory(
    val imageUri: String,
    val diagnosis: String,
    val explanation: String,
    val suggestion: String,
    val dateDetected: String
)