package com.bangkit.fishmate.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fishmate.data.Response.DetectionHistory
import com.bangkit.fishmate.databinding.ItemHistoryBinding

class HistoryAdapter(private var historyList: List<DetectionHistory>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size

    fun updateHistoryList(newHistoryList: List<DetectionHistory>) {
        this.historyList = newHistoryList
        notifyDataSetChanged()  // Notify that the data has changed
    }


    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: DetectionHistory) {
            binding.historyImageView.setImageURI(Uri.parse(history.imageUri))
            binding.historyDiagnosisTextView.text = "Diagnosis: ${history.diagnosis}"
            binding.historyDateTextView.text = "Date: ${history.dateDetected}"
        }
    }
}
