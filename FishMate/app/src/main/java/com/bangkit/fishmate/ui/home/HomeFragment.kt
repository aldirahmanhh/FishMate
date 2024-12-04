package com.bangkit.fishmate.ui.home

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
import com.bangkit.fishmate.adapter.NewsAdapter
import com.bangkit.fishmate.data.ApiConfig
import com.bangkit.fishmate.data.Response.NewsResponse
import com.bangkit.fishmate.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
