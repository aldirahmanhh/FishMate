package com.bangkit.fishmate.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.fishmate.R
import com.bangkit.fishmate.adapter.HistoryAdapter
import com.bangkit.fishmate.data.Response.DetectionHistory
import com.bangkit.fishmate.data.SharedPrefHelper
import com.bangkit.fishmate.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private val historyAdapter: HistoryAdapter by lazy {
        HistoryAdapter(getHistoryList())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loading.visibility = View.VISIBLE
        // Initialize SharedPreferences
        sharedPrefHelper = SharedPrefHelper(this)

        // Setup RecyclerView
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = historyAdapter

        // Setup Clear History Button
        binding.clearButton.setOnClickListener {
            clearHistory()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getHistoryList(): List<DetectionHistory> {
        binding.loading.visibility = View.GONE
        return sharedPrefHelper.getHistory()
    }

    private fun clearHistory() {
        sharedPrefHelper.clearHistory()  // Clear history from SharedPreferences
        Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show()

        // After clearing the history, update the adapter with an empty list
        historyAdapter.updateHistoryList(emptyList())

        // Notify RecyclerView to update
        historyAdapter.notifyDataSetChanged()
    }

}
