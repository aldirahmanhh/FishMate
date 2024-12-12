package com.bangkit.fishmate.data

import com.bangkit.fishmate.data.Response.Product
import com.bangkit.fishmate.data.Response.ProductResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ProductConfig {
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun fetchProducts(page: Int): List<Product> {
        return withContext(Dispatchers.IO) {
            val url = "https://real-time-product-search.p.rapidapi.com/search-v2?q=fish%20medication&country=us&language=en&page=$page&limit=10&sort_by=BEST_MATCH&product_condition=ANY"

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", "8b821e2427msh076cac92c60aa48p128387jsneb8444953fbe")
                .addHeader("x-rapidapi-host", "real-time-product-search.p.rapidapi.com")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val productResponse = gson.fromJson(responseBody, ProductResponse::class.java)
                return@withContext productResponse.data.products
            } else {
                return@withContext emptyList()
            }
        }
    }
}
