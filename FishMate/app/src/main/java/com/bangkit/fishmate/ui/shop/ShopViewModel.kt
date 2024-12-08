package com.bangkit.fishmate.ui.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import com.bangkit.fishmate.data.ProductConfig
import com.bangkit.fishmate.data.Response.Product
class ShopViewModel : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    private val productConfig = ProductConfig() // This will call the ApiService

    fun fetchProducts(refresh: Boolean = false) {
        if (isLoading || isLastPage) return

        if (refresh) {
            currentPage = 1
            isLastPage = false
            _products.value = emptyList() // Clear existing data before refreshing
        }

        isLoading = true
        viewModelScope.launch {
            try {
                // Pass the current page to fetch the products
                val result = productConfig.fetchProducts(currentPage)
                if (result != null && result.isNotEmpty()) {
                    if (refresh) {
                        _products.value = result // Replace data on refresh
                    } else {
                        _products.value = (_products.value ?: emptyList()) + result // Append new data
                    }

                    // Check if there's more data to load
                    if (result.size < 10) {
                        isLastPage = true
                    } else {
                        currentPage++
                    }
                }
            } catch (e: Exception) {
                Log.e("ShopViewModel", "Error fetching products", e)
            } finally {
                isLoading = false
            }
        }
    }
}
