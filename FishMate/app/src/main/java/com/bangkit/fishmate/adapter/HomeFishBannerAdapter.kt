package com.bangkit.fishmate.adapter

import android.os.Bundle
import android.provider.Settings.Global.putInt
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fishmate.R
import com.bangkit.fishmate.ui.home.HomeFragment

class HomeFishBannerAdapter(
    private val imagesWithIds: List<Pair<Int, Int>> // Pair<ImageResId, FishId>
) : RecyclerView.Adapter<HomeFishBannerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.vp_item_banner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fishbanner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (imageResId, fishId) = imagesWithIds[position]
        holder.imageView.setImageResource(imageResId)

        holder.imageView.setOnClickListener { view ->
            Log.d("HomeFishBannerAdapter", "Clicked Fish ID: $fishId")
            val fishId = imagesWithIds[position].second

            val bundle = Bundle().apply {
                putInt("fishId", fishId)
            }
            view.findNavController().navigate(R.id.navigation_detail_fish_banner, bundle)
        }


    }

    override fun getItemCount(): Int = imagesWithIds.size
}

