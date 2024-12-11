package com.bangkit.fishmate.data

import android.content.Context
import android.content.SharedPreferences
import com.bangkit.fishmate.data.Response.DetectionHistory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

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

    // Save history
    fun saveHistory(historyList: List<DetectionHistory>) {
        val json = gson.toJson(historyList)
        sharedPreferences.edit().putString("detection_history", json).apply()
    }

    // Get history
    fun getHistory(): List<DetectionHistory> {
        val json = sharedPreferences.getString("detection_history", null)
        return if (json != null) {
            val type = object : TypeToken<List<DetectionHistory>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Clear diagnosis history method
    fun clearHistory() {
        sharedPreferences.edit().remove("history_list").apply()
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
