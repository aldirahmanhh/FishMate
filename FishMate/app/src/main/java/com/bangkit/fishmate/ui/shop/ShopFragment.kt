package com.bangkit.fishmate.ui.shop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fishmate.adapter.ProductAdapter
import com.bangkit.fishmate.data.Response.Product
import com.bangkit.fishmate.databinding.FragmentShopBinding

class ShopFragment : Fragment() {

    private lateinit var shopViewModel: ShopViewModel
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentShopBinding.inflate(inflater, container, false)

        // RecyclerView setup
        binding.rvFishRecomendation.layoutManager = LinearLayoutManager(context)
        productAdapter = ProductAdapter(productList) { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        binding.rvFishRecomendation.adapter = productAdapter

        // SwipeRefresh setup
        binding.swipeRefreshLayout.setOnRefreshListener {
            shopViewModel.fetchProducts(refresh = true) // Refresh data
        }

        // Get ViewModel
        shopViewModel = ViewModelProvider(this).get(ShopViewModel::class.java)

        // Observer for product data
        shopViewModel.products.observe(viewLifecycleOwner) { products ->
            productList.clear()
            productList.addAll(products)
            productAdapter.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false // Stop the refresh animation
        }

        // Initial data fetch
        shopViewModel.fetchProducts()

        // Infinite scroll pagination
        binding.rvFishRecomendation.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                // Load more data when the user is near the bottom
                if (!isLoading && totalItemCount <= (pastVisibleItems + 5)) {
                    isLoading = true
                    shopViewModel.fetchProducts(refresh = false)
                }
            }
        })

        return binding.root
    }
}
