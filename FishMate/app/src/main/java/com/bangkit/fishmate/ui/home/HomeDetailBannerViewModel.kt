package com.bangkit.fishmate.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bangkit.fishmate.data.ApiConfig
import com.bangkit.fishmate.data.Response.BannerData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.fishmate.data.AuthConfig
import com.bangkit.fishmate.data.Response.FishBannerResponse

class HomeDetailBannerViewModel : ViewModel() {

    private val _fishData = MutableLiveData<BannerData?>()
    val fishData: LiveData<BannerData?> get() = _fishData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchFishDetail(fishId: Int?) {
        if (fishId == null) {
            _errorMessage.postValue("Invalid fish ID")
            return
        }

        val call = AuthConfig.api.getFishDetail(fishId)
        call.enqueue(object : Callback<FishBannerResponse> {
            override fun onResponse(call: Call<FishBannerResponse>, response: Response<FishBannerResponse>) {
                if (response.isSuccessful) {
                    val fishResponse = response.body()
                    if (fishResponse?.success == true) {
                        _fishData.postValue(fishResponse.data) // Update LiveData with "data"
                    } else {
                        _errorMessage.postValue("Failed to load fish details")
                    }
                } else {
                    _errorMessage.postValue("Response Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FishBannerResponse>, t: Throwable) {
                _errorMessage.postValue("Error: ${t.message}")
            }
        })
    }
}

