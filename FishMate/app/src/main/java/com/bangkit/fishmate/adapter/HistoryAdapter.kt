package com.bangkit.fishmate.adapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fishmate.R
import com.bangkit.fishmate.data.Response.DetectionHistory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

class HistoryAdapter(private var historyList: List<DetectionHistory>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentHistory = historyList[position]

        Log.d("HistoryAdapter", "Loading image URI: ${currentHistory.imageUri}")
        Glide.with(holder.itemView.context)
            .load(Uri.parse(currentHistory.imageUri))
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    holder.imageView.setImageDrawable(resource)
                    Log.d("HistoryAdapter", "Image loaded successfully.")
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Log.e("HistoryAdapter", "Image load failed.")
                }
            })


        holder.diagnosisTextView.text = currentHistory.diagnosis
        holder.dateTextView.text = currentHistory.dateDetected
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun updateHistoryList(newHistoryList: List<DetectionHistory>) {
        historyList = newHistoryList
        notifyDataSetChanged()
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.historyImageView)
        val diagnosisTextView: TextView = itemView.findViewById(R.id.diagnosisTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }
}
