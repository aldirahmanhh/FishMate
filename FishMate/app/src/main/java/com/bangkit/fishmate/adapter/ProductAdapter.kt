package com.bangkit.fishmate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fishmate.data.Response.Product
import com.bangkit.fishmate.databinding.ItemProductBinding
import com.bumptech.glide.Glide

class ProductAdapter(
    private val productList: List<Product>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            // Set title, description, and price
            binding.productTitle.text = product.product_title
            binding.productDescription.text = product.product_description
            binding.productPrice.text = product.offer.price

            // Set product rating
            binding.productRating.rating = product.product_rating.toFloat()

            // Load the first image from the list of product photos using Glide
            if (product.product_photos.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(product.product_photos.first())
                    .into(binding.productImage)
            }

            // Handle product item click
            binding.root.setOnClickListener {
                // Open the product page URL
                onItemClick(product.product_page_url)
            }
        }
    }
}
