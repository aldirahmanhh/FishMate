package com.bangkit.fishmate.ui.home

import HomeFishBannerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.fishmate.R
import com.bangkit.fishmate.adapter.NewsAdapter
import com.bangkit.fishmate.data.ApiConfig
import com.bangkit.fishmate.data.Response.NewsResponse
import com.bangkit.fishmate.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup RecyclerView
        newsAdapter = NewsAdapter()
        binding.rvNewsRecomendation.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvNewsRecomendation.adapter = newsAdapter

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvNewsRecomendation)

        // Fetch News
        fetchNews()
        setupViewPager()

        return binding.root
    }

    private fun fetchNews() {
        val call = ApiConfig.api.getNews("ikan indonesia", "bf4374ec295e42a99952261bef02bbb9")
        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        // Set adapter dengan berita penyakit ikan
                        newsAdapter.setNews(it.articles.take(10)) //Max 5 news
                    }
                } else {
                    showToast("Failed to load news")
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    fun setupViewPager (){
        val viewPager: ViewPager2 = binding.vpHomeBanner
        val dotsIndicator: DotsIndicator = binding.dotsHomeBanner

        val images = listOf(
            R.drawable.banner_ikan_nila,
            R.drawable.banner_ikan_lele,
        )

        val adapter = HomeFishBannerAdapter(this, images)
        viewPager.adapter = adapter

        dotsIndicator.attachTo(viewPager)

        viewPager.setPageTransformer { page, position ->
            when {
                position <= -1 || position >= 1 -> {
                    page.alpha = 0f
                }
                position == 0f -> {
                    page.alpha = 1f
                }
                else -> {
                    page.alpha = 1 - Math.abs(position)
                }
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
