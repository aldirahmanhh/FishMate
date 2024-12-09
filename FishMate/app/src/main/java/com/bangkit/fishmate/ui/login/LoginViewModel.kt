package com.bangkit.fishmate.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bangkit.fishmate.data.AuthConfig
import com.bangkit.fishmate.data.Response.LoginRequest
import com.bangkit.fishmate.data.Response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    fun login(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val loginRequest = LoginRequest(email, password)

        AuthConfig.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && !loginResponse.error) {
                        onSuccess(loginResponse.message)
                    } else {
                        onError(loginResponse?.message ?: "Login failed")
                    }
                } else {
                    onError("Server error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginViewModel", "Error: ${t.localizedMessage}")
                onError("Network error: ${t.localizedMessage}")
            }
        })
    }
}
