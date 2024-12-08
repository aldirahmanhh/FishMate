package com.bangkit.fishmate.data

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Save data methods
    fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit().putString("username", username).apply()
    }

    fun saveEmail(email: String) {
        sharedPreferences.edit().putString("email", email).apply()
    }

    fun saveDarkMode(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean("dark_mode", isDarkMode).apply()
    }

    // Get data methods
    fun getToken(): String? {
        return getString("auth_token")
    }

    fun getUsername(): String? {
        return getString("username")
    }

    fun getEmail(): String? {
        return getString("email")
    }

    fun getDarkMode(): Boolean {
        return sharedPreferences.getBoolean("dark_mode", false)
    }

    // Helper method to retrieve string values from SharedPreferences
    private fun getString(key: String, default: String? = null): String? {
        return sharedPreferences.getString(key, default)
    }

    // Check if user is logged in based on token presence
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    // Clear all saved data
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
