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

    // Fungsi login
    fun login(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        // Membuat request login
        val loginRequest = LoginRequest(email, password)

        // Mengirim request ke API dengan Retrofit
        AuthConfig.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                // Mengecek apakah response berhasil dan valid
                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    // Mengecek apakah response body tidak null
                    if (loginResponse != null && loginResponse.token.isNotEmpty()) {
                        // Jika login sukses dan token ada
                        onSuccess("Login Berhasil!")
                    } else {
                        // Jika response tidak mengandung token
                        onError("Login gagal, token tidak ditemukan.")
                    }
                } else {
                    // Jika response dari server gagal
                    onError("Gagal terhubung ke server. Status: ${response.message()}")
                }
            }

            // Menangani jika request gagal
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginViewModel", "Error: ${t.localizedMessage}")
                // Mengirimkan pesan error ke UI
                onError("Terjadi kesalahan: ${t.localizedMessage}")
            }
        })
    }
}
