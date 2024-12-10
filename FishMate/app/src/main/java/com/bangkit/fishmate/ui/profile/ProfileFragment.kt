package com.bangkit.fishmate.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.bangkit.fishmate.data.SharedPrefHelper
import com.bangkit.fishmate.databinding.FragmentProfileBinding
import com.bangkit.fishmate.ui.changeAuth.ChangePassword
import com.bangkit.fishmate.ui.login.LoginActivity
import com.bangkit.fishmate.ui.theme.ThemeManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var sharedPrefHelper: SharedPrefHelper? = null
    private lateinit var themeManager: ThemeManager

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        sharedPrefHelper = SharedPrefHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.usernameText.text = sharedPrefHelper?.getUsername()
        binding.emailText.text = sharedPrefHelper?.getEmail()

        binding.buttonChangePassword.setOnClickListener {
            val intent = Intent(requireContext(), ChangePassword::class.java)
            startActivity(intent)
        }

        themeManager = ThemeManager(requireContext())
        themeManager.themeMode.asLiveData().observe(viewLifecycleOwner) { isDarkMode ->
            binding.switchTheme.isChecked = isDarkMode
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                themeManager.saveTheme(isChecked)
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        binding.buttonLogout.setOnClickListener {
            sharedPrefHelper?.clear()
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}