package com.bangkit.fishmate.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.fishmate.data.AuthConfig
import com.bangkit.fishmate.data.Response.BannerData
import com.bangkit.fishmate.data.Response.FishBannerResponse
import com.bangkit.fishmate.data.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeDetailBannerViewModel(application: Application) : AndroidViewModel(application) {

    private val _fishData = MutableLiveData<BannerData?>()
    val fishData: LiveData<BannerData?> get() = _fishData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val sharedPrefHelper = SharedPrefHelper(application)

    fun fetchFishDetail(fishId: Int?) {
        if (fishId == null) {
            _errorMessage.postValue("Invalid fish ID")
            return
        }

        // Retrieve the authentication token from SharedPreferences
        val token = sharedPrefHelper.getToken()

        if (token.isNullOrEmpty()) {
            _errorMessage.postValue("No authentication token found")
            return
        }

        val call = AuthConfig.api.getFishDetail("Bearer $token", fishId)
        call.enqueue(object : Callback<FishBannerResponse> {
            override fun onResponse(call: Call<FishBannerResponse>, response: Response<FishBannerResponse>) {
                if (response.isSuccessful) {
                    val fishResponse = response.body()
                    if (fishResponse?.success == true) {
                        _fishData.postValue(fishResponse.data)
                    } else {
                        _errorMessage.postValue("Failed to load fish details: ${fishResponse?.message}")
                    }
                } else {
                    _errorMessage.postValue("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<FishBannerResponse>, t: Throwable) {
                _errorMessage.postValue("Failed to connect: ${t.message}")
            }
        })
    }
}
