package com.bangkit.fishmate.data.Response

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

data class LoginRequest(
    val email: String,
    val password: String
)

data class ApiResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

data class LoginResponse(
    val message: String,
    val token: String
)

data class RegisterResponse(
    val message: String
)
