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

    // Fungsi untuk mengambil data produk dengan pagination
    suspend fun fetchProducts(page: Int): List<Product> {
        return withContext(Dispatchers.IO) {
            // Dynamically update the URL with the current page
            val url = "https://real-time-product-search.p.rapidapi.com/search-v2?q=fish%20medication&country=us&language=en&page=$page&limit=10&sort_by=BEST_MATCH&product_condition=ANY"

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", "fd3588e7f0msh20c8ec63a0e8874p18da5bjsn455d61a2c83b")
                .addHeader("x-rapidapi-host", "real-time-product-search.p.rapidapi.com")
                .build()

            // Menjalankan request dan mendapatkan response
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                // Parsing JSON ke objek ProductResponse
                val productResponse = gson.fromJson(responseBody, ProductResponse::class.java)
                return@withContext productResponse.data.products ?: emptyList() // Return an empty list if null
            } else {
                return@withContext emptyList() // Return an empty list if the request fails
            }
        }
    }
}
