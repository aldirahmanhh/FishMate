package com.bangkit.fishmate.ui.home

import android.content.Intent
import com.bangkit.fishmate.adapter.HomeFishBannerAdapter
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
import com.bangkit.fishmate.data.SharedPrefHelper
import com.bangkit.fishmate.databinding.FragmentHomeBinding
import com.bangkit.fishmate.ui.news.NewsPageActivity
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var sharedPrefHelper : SharedPrefHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        sharedPrefHelper = SharedPrefHelper(requireContext())

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvUserName.text = "Hello, ${sharedPrefHelper.getUsername()}!"

        newsAdapter = NewsAdapter { url ->
            openWebView(url)
        }

        binding.rvNewsRecomendation.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvNewsRecomendation.adapter = newsAdapter

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvNewsRecomendation)

        homeViewModel.newsList.observe(viewLifecycleOwner) { news ->
            newsAdapter.setNews(news)
        }

        homeViewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        homeViewModel.fetchNews()
        setupViewPager()

        return binding.root
    }



    fun setupViewPager() {
        val viewPager: ViewPager2 = binding.vpHomeBanner
        val dotsIndicator: DotsIndicator = binding.dotsHomeBanner

        val imagesWithIds = listOf(
            Pair(R.drawable.banner_ikan_nila, 1),
            Pair(R.drawable.banner_ikan_lele, 2),
            Pair(R.drawable.banner_ikan_gurame, 3)
        )

        val adapter = HomeFishBannerAdapter(imagesWithIds)
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openWebView(url: String) {
        val intent = Intent(requireContext(), NewsPageActivity::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }
}
