package com.bangkit.fishmate.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.fishmate.MainActivity
import com.bangkit.fishmate.R
import com.bangkit.fishmate.data.AuthConfig
import com.bangkit.fishmate.data.Response.LoginRequest
import com.bangkit.fishmate.data.Response.LoginResponse
import com.bangkit.fishmate.data.SharedPrefHelper
import com.bangkit.fishmate.databinding.ActivityLoginBinding
import com.bangkit.fishmate.ui.register.RegisterActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loading.visibility = View.GONE
        sharedPrefHelper = SharedPrefHelper(this)

        // Check if already logged in by checking for saved token
        if (sharedPrefHelper.isLoggedIn()) {
            navigateToMainActivity()
        }

        binding.register?.setOnClickListener {
            // Gk perlu loading sih, cuma pindah page
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.login.setOnClickListener {
            val usernameEditText = findViewById<TextInputEditText>(R.id.username)
            val passwordEditText = findViewById<TextInputEditText>(R.id.password)
            val email = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.loading.visibility = View.VISIBLE
                loginUser(email, password)
            } else {
                showToast("Please enter email and password")
                binding.loading.visibility = View.GONE
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        AuthConfig.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    binding.loading.visibility = View.GONE
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.token.isNotEmpty()) {
                        sharedPrefHelper.saveToken(loginResponse.token)

                        // Simpan username dan email setelah login berhasil
                        sharedPrefHelper.saveUsername(email) // Username bisa diganti sesuai API
                        sharedPrefHelper.saveEmail(email)

                        navigateToMainActivity()
                    } else {
                        binding.loading.visibility = View.GONE
                        showToast("Login failed. Token not received.")
                    }
                } else {
                    binding.loading.visibility = View.GONE
                    showToast("Login failed. Server error.")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showToast("Network request failed: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMainActivity() {
        // Navigate to MainActivity if login is successful
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.loading.visibility = View.GONE
    }
}
