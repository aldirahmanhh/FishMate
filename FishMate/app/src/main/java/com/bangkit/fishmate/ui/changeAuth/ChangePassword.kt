package com.bangkit.fishmate.ui.changeAuth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.fishmate.data.AuthConfig
import com.bangkit.fishmate.data.Response.ChangePasswordRequest
import com.bangkit.fishmate.data.Response.ChangePasswordResponse
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

        // Handle Save Password button click
        binding.buttonChangePassword.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val currentPassword = binding.password.text.toString().trim()
            val newPassword = binding.newPassword.text.toString().trim()
            val confirmPassword = binding.confirmPassword.text.toString().trim()

            // Input validation
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
    }

    private fun updatePassword(request: ChangePasswordRequest) {
        binding.loading.visibility = View.VISIBLE

        AuthConfig.api.changePassword(request).enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                binding.loading.visibility = View.GONE
                if (response.isSuccessful) {
                    val changePasswordResponse = response.body()
                    if (changePasswordResponse != null && !changePasswordResponse.error) {
                        showToast(changePasswordResponse.message)
                        finish()
                    } else {
                        showToast("Failed to update password")
                    }
                } else {
                    showToast("Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                binding.loading.visibility = View.GONE
                showToast("Network error: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
