package com.bangkit.fishmate.ui.shop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentShopBinding.inflate(inflater, container, false)

        // RecyclerView setup
        binding.rvFishRecomendation.layoutManager = LinearLayoutManager(context)
        productAdapter = ProductAdapter(productList) { url ->
            // Show WebView and load the URL
            binding.swipeRefreshLayout.visibility = View.GONE
            binding.webView.apply {
                visibility = View.VISIBLE
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, requestUrl: String): Boolean {
                        view.loadUrl(requestUrl)
                        return true
                    }
                }
                loadUrl(url)
            }
        }
        binding.rvFishRecomendation.adapter = productAdapter

        // SwipeRefresh setup
        binding.swipeRefreshLayout.setOnRefreshListener {
            shopViewModel.fetchProducts(refresh = true) // Refresh data
        }

        // WebView back navigation
        binding.webView.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP && binding.webView.canGoBack()) {
                binding.webView.goBack()
                true
            } else {
                binding.webView.visibility = View.GONE
                binding.swipeRefreshLayout.visibility = View.VISIBLE
                false
            }
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
