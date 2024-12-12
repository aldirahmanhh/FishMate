package com.bangkit.fishmate.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.fishmate.adapter.HistoryAdapter
import com.bangkit.fishmate.databinding.ActivityHistoryBinding
import android.net.Uri
import com.bangkit.fishmate.repository.HistoryRepository

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the repository
        historyRepository = HistoryRepository(this)

        // Get history data from the repository
        val historyList = historyRepository.getHistory()

        historyAdapter = HistoryAdapter(historyList)
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = historyAdapter

        binding.clearButton.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            clearHistory()
            binding.loading.visibility = View.GONE
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.loading.visibility = View.GONE
    }

    // Clear history and update RecyclerView
    private fun clearHistory() {
        val currentHistory = historyRepository.getHistory()

        if (currentHistory.size >= 20) {
            showClearHistoryDialog {
                historyRepository.clearHistory()
                Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show()

                // Update the adapter after clearing
                historyAdapter.updateHistoryList(emptyList())
                historyAdapter.notifyDataSetChanged()

                binding.loading.visibility = View.GONE
            }
        } else {
            historyRepository.clearHistory()
            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show()

            // Update the adapter after clearing
            historyAdapter.updateHistoryList(emptyList())
            historyAdapter.notifyDataSetChanged()

            binding.loading.visibility = View.GONE
        }
    }

    private fun showClearHistoryDialog(onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Riwayat sudah mencapai batas 20. Apakah Anda ingin menghapus riwayat pertama?")
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                onConfirm()
            }
            .setNegativeButton("Tidak") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}
