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
    val offer_id: String,
    val offer_page_url: String,
    val price: String,
    val shipping: String,
    val store_name: String,
    val store_rating: String,
    val store_review_count: Int,
    val store_reviews_page_url: String
)

data class ProductData(
    val products: List<Product>
)

data class ProductResponse(
    val status: String,
    val request_id: String,
    val data: ProductData
)
