package com.bangkit.fishmate.ui.register

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.fishmate.databinding.ActivityRegisterBinding
import com.bangkit.fishmate.data.ApiService
import com.bangkit.fishmate.data.Response.RegisterRequest
import com.bangkit.fishmate.data.Response.RegisterResponse
import com.bangkit.fishmate.data.AuthConfig
import com.bangkit.fishmate.data.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper // Properti sharedPrefHelper harus diinisialisasi dengan benar

    private val apiService: ApiService by lazy {
        AuthConfig.api
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pastikan sharedPrefHelper diinisialisasi di sini
        sharedPrefHelper = SharedPrefHelper(this)

        binding.loading.visibility = ProgressBar.GONE

        // Register Button Click Listener
        binding.register.setOnClickListener {
            val username = binding.username.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            binding.loading.visibility = ProgressBar.VISIBLE
            // Validate inputs
            if (TextUtils.isEmpty(username)) {
                binding.username.error = "Username is required"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(email)) {
                binding.email.error = "Email is required"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                binding.password.error = "Password is required"
                return@setOnClickListener
            }

            val registerRequest = RegisterRequest(username, email, password)
            registerUser(registerRequest)
        }

        // Login Link Click Listener
        binding.tvLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun registerUser(registerRequest: RegisterRequest) {
        apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    showToast("Registration Successful")

                    // Simpan username dan email setelah register berhasil
                    sharedPrefHelper.saveUsername(registerRequest.username)
                    sharedPrefHelper.saveEmail(registerRequest.email)
                    binding.loading.visibility = ProgressBar.GONE

                    finish()
                } else {
                    Log.e("RegisterActivity", "Error: ${response.code()}")
                    showToast("Registration Failed")
                    binding.loading.visibility = ProgressBar.GONE
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showToast("Registration Failed")
                Log.e("RegisterActivity", "Error: ${t.localizedMessage}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
