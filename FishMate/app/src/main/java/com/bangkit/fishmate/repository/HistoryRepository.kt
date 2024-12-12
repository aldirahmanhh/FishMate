package com.bangkit.fishmate.repository

import android.content.Context
import com.bangkit.fishmate.data.Response.DetectionHistory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryRepository(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("FishMatePrefs", Context.MODE_PRIVATE)

    // Save history: Convert List<DetectionHistory> to JSON string
    fun saveHistory(historyList: List<DetectionHistory>) {
        val json = Gson().toJson(historyList)
        sharedPreferences.edit().putString("history", json).apply()
    }

    // Get history: Convert JSON string back to List<DetectionHistory>
    fun getHistory(): List<DetectionHistory> {
        val json = sharedPreferences.getString("history", "[]") ?: "[]"
        val type = object : TypeToken<List<DetectionHistory>>() {}.type
        return Gson().fromJson(json, type)
    }

    // Clear history
    fun clearHistory() {
        sharedPreferences.edit().remove("history").apply()
    }
}
