package com.bangkit.fishmate.ui.changeAuth

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.fishmate.R
import com.bangkit.fishmate.data.AuthConfig
import com.bangkit.fishmate.data.Response.ForgotPasswordRequest
import com.bangkit.fishmate.data.Response.ForgotPasswordResponse
import com.bangkit.fishmate.data.Response.ResetPasswordRequest
import com.bangkit.fishmate.data.Response.ResetPasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPassword : AppCompatActivity() {

    private lateinit var emailTitle: TextView
    private lateinit var tokenTitle: TextView
    private lateinit var newPasswordTitle: TextView
    private lateinit var confirmPasswordTitle: TextView
    private lateinit var email: EditText
    private lateinit var token: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var buttonAction: Button
    private lateinit var progressBar: ProgressBar

    private var isResetMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        emailTitle = findViewById(R.id.emailTitle)
        tokenTitle = findViewById(R.id.tokenTitle)
        newPasswordTitle = findViewById(R.id.newPasswordTitle)
        confirmPasswordTitle = findViewById(R.id.confirmPasswordTitle)

        email = findViewById(R.id.email)
        token = findViewById(R.id.token)
        newPassword = findViewById(R.id.newPassword)
        confirmPassword = findViewById(R.id.confirmPassword)
        buttonAction = findViewById(R.id.button_action)
        progressBar = findViewById(R.id.loading)

        buttonAction.setOnClickListener {
            if (isResetMode) {
                progressBar.visibility = View.VISIBLE
                performResetPassword()
                progressBar.visibility = View.GONE
            } else {
                progressBar.visibility = View.VISIBLE
                performForgotPassword()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun performForgotPassword() {
        val emailInput = email.text.toString()

        if (emailInput.isEmpty()) {
            email.error = getString(R.string.mail_required)
            return
        }

        progressBar.visibility = View.VISIBLE

        val request = ForgotPasswordRequest(emailInput)
        AuthConfig.api.forgotPassword(request).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(
                call: Call<ForgotPasswordResponse>,
                response: Response<ForgotPasswordResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.error == false) {
                    Toast.makeText(
                        this@ForgotPassword,
                        response.body()?.message ?: "Success",
                        Toast.LENGTH_SHORT
                    ).show()
                    switchToResetMode()
                } else {
                    Toast.makeText(
                        this@ForgotPassword,
                        response.body()?.message ?: "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ForgotPassword, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun performResetPassword() {
        val emailInput = email.text.toString()
        val tokenInput = token.text.toString()
        val newPasswordInput = newPassword.text.toString()
        val confirmPasswordInput = confirmPassword.text.toString()

        if (tokenInput.isEmpty()) {
            token.error = getString(R.string.token_required)
            return
        }

        if (newPasswordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPasswordInput != confirmPasswordInput) {
            confirmPassword.error = getString(R.string.password_not_match)
            return
        }

        val request = ResetPasswordRequest(
            email = emailInput,
            token = tokenInput,
            newPassword = newPasswordInput,
            confirmPassword = confirmPasswordInput
        )
        AuthConfig.api.resetPassword(request).enqueue(object : Callback<ResetPasswordResponse> {
            override fun onResponse(
                call: Call<ResetPasswordResponse>,
                response: Response<ResetPasswordResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.error == false) {
                    Toast.makeText(
                        this@ForgotPassword,
                        response.body()?.message ?: "Password Reset Success",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@ForgotPassword,
                        response.body()?.message ?: "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ForgotPassword, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun switchToResetMode() {
        isResetMode = true

        // Hide email input
        emailTitle.visibility = View.GONE
        email.visibility = View.GONE

        // Show token and password inputs
        tokenTitle.visibility = View.VISIBLE
        newPasswordTitle.visibility = View.VISIBLE
        confirmPasswordTitle.visibility = View.VISIBLE
        token.visibility = View.VISIBLE
        newPassword.visibility = View.VISIBLE
        confirmPassword.visibility = View.VISIBLE

        // Update button text
        buttonAction.text = getString(R.string.change_password)
        buttonAction.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            performResetPassword()
            progressBar.visibility = View.GONE
        }
    }
}
