package com.bangkit.fishmate.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.fishmate.data.ApiConfig
import com.bangkit.fishmate.data.Response.Article
import com.bangkit.fishmate.data.Response.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _newsList = MutableLiveData<List<Article>>()
    val newsList: LiveData<List<Article>> get() = _newsList

    fun fetchNews() {
        val call = ApiConfig.api.getNews("ikan indonesia", "bf4374ec295e42a99952261bef02bbb9")
        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _newsList.value = it.articles.take(10)
                    }
                } else {
                    _toastMessage.value = "Failed to load news"
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                _toastMessage.value = "Error: ${t.message}"
            }
        })
    }
}
