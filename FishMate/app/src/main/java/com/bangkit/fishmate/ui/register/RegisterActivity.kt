package com.bangkit.fishmate.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.fishmate.data.AuthConfig
import com.bangkit.fishmate.data.Response.RegisterRequest
import com.bangkit.fishmate.data.Response.RegisterResponse
import com.bangkit.fishmate.data.SharedPrefHelper
import com.bangkit.fishmate.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefHelper = SharedPrefHelper(this)

        binding.loading.visibility = View.GONE

        binding.register.setOnClickListener {
            val username = binding.usernameLayout.text.toString().trim()
            val email = binding.emailLayout.text.toString().trim()
            val password = binding.passwordLayout.text.toString().trim()

            if (username.isEmpty()) {
                binding.usernameLayout.error = "Username is required"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.emailLayout.error = "Email is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordLayout.error = "Password is required"
                return@setOnClickListener
            }

            binding.loading.visibility = View.VISIBLE
            val registerRequest = RegisterRequest(username, email, password)
            registerUser(registerRequest)
        }

        binding.tvLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun registerUser(registerRequest: RegisterRequest) {
        AuthConfig.api.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                binding.loading.visibility = View.GONE
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null && !registerResponse.error) {
                        sharedPrefHelper.saveUsername(registerRequest.username)
                        sharedPrefHelper.saveEmail(registerRequest.email)
                        showToast("Registration Successful")
                        finish()
                    } else {
                        showToast(registerResponse?.message ?: "Registration failed")
                    }
                } else {
                    showToast("Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                binding.loading.visibility = View.GONE
                showToast("Network request failed: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
