package com.bangkit.fishmate.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fishmate.data.Response.Product
import com.bangkit.fishmate.databinding.ItemProductBinding
import com.bangkit.fishmate.ui.shop.WebViewActivity
import com.bumptech.glide.Glide

class ProductAdapter(
    private val context: Context,
    private val productList: List<Product>
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
            binding.productTitle.text = product.product_title
            binding.productDescription.text = product.product_description
            binding.productPrice.text = product.offer.price

            binding.productRating.rating = product.product_rating.toFloat()

            if (product.product_photos.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(product.product_photos.first())
                    .into(binding.productImage)
            }

            binding.root.setOnClickListener {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("PRODUCT_URL", product.product_page_url)
                context.startActivity(intent)
            }
        }
    }
}
