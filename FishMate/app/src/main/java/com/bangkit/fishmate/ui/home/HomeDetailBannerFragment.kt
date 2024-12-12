package com.bangkit.fishmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.fishmate.databinding.FragmentHomeDetailBannerBinding
import com.bumptech.glide.Glide

class HomeDetailBannerFragment : Fragment() {

    private var _binding: FragmentHomeDetailBannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeDetailBannerViewModel
    private var fishId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        fishId = arguments?.getInt("fishId", -1)
        Log.d("HomeDetailFragment", "Received Fish ID: $fishId")

        if (fishId != -1) {
            viewModel = ViewModelProvider(this).get(HomeDetailBannerViewModel::class.java)
            viewModel.fetchFishDetail(fishId)
        } else {
            Toast.makeText(requireContext(), "Invalid Fish ID", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeDetailBannerBinding.inflate(inflater, container, false)
        val progressBar = binding.progressBar

        progressBar.visibility = View.VISIBLE

        setupObservers()

        binding.fabBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun setupObservers() {
        val progressBar = binding.progressBar
        viewModel.fishData.observe(viewLifecycleOwner) { fishData ->
            if (fishData != null) {
                progressBar.visibility = View.GONE
                binding.titleFish.text = fishData.nama ?: "No Title"
                binding.descFish.text = fishData.deskripsi ?: "No Description"

                fishData.gambar?.let { imageUrl ->
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.fishBannerDetail)
                }
            } else {
                progressBar.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
