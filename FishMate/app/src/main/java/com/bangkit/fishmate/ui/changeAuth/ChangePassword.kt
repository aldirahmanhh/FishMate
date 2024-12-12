package com.bangkit.fishmate.ui.changeAuth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.fishmate.data.AuthConfig
import com.bangkit.fishmate.data.Response.ChangePasswordRequest
import com.bangkit.fishmate.data.Response.ChangePasswordResponse
import com.bangkit.fishmate.data.SharedPrefHelper
import com.bangkit.fishmate.databinding.ActivityChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePassword : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonChangePassword.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val currentPassword = binding.password.text.toString().trim()
            val newPassword = binding.newPassword.text.toString().trim()
            val confirmPassword = binding.confirmPassword.text.toString().trim()

            if (email.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showToast("All fields are required")
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                showToast("New password and confirmation password do not match")
                return@setOnClickListener
            }

            val request = ChangePasswordRequest(email, currentPassword, newPassword, confirmPassword)
            updatePassword(request)
        }

        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun updatePassword(request: ChangePasswordRequest) {

        binding.loading.visibility = View.VISIBLE

        val token = "Bearer ${SharedPrefHelper(this).getToken()}"

        AuthConfig.api.changePassword(token, request).enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                binding.loading.visibility = View.GONE

                if (response.isSuccessful) {
                    val changePasswordResponse = response.body()
                    if (changePasswordResponse != null && !changePasswordResponse.error) {
                        showToast("Password successfully updated")
                        finish()
                    } else {
                        showToast(changePasswordResponse?.message ?: "Failed to update password")
                    }
                } else {
                    showToast("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                binding.loading.visibility = View.GONE
                showToast("Request failed: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
