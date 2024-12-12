package com.bangkit.fishmate.ui.shop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.fishmate.adapter.ProductAdapter
import com.bangkit.fishmate.data.Response.Product
import com.bangkit.fishmate.databinding.FragmentShopBinding

class ShopFragment : Fragment() {

    private lateinit var shopViewModel: ShopViewModel
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentShopBinding.inflate(inflater, container, false)

        binding.progressBar.visibility = View.VISIBLE

        binding.rvFishRecomendation.layoutManager = LinearLayoutManager(context)
        productAdapter = ProductAdapter(requireContext(), productList)
        binding.rvFishRecomendation.adapter = productAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            shopViewModel.fetchProducts(refresh = true)
        }
        shopViewModel = ViewModelProvider(this).get(ShopViewModel::class.java)

        shopViewModel.products.observe(viewLifecycleOwner) { products ->
            productList.clear()
            productList.addAll(products)
            productAdapter.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false
            binding.progressBar.visibility = View.GONE
        }

        shopViewModel.fetchProducts()

        return binding.root
    }
}