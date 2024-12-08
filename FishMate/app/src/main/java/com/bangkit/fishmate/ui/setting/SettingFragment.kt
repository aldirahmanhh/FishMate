package com.bangkit.fishmate.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bangkit.fishmate.data.SharedPrefHelper
import com.bangkit.fishmate.databinding.FragmentSettingBinding
import com.bangkit.fishmate.ui.login.LoginActivity
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private var sharedPrefHelper: SharedPrefHelper? = null

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        sharedPrefHelper = SharedPrefHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.usernameText.text = sharedPrefHelper?.getUsername()
        binding.emailText.text = sharedPrefHelper?.getEmail()

        val isDarkMode = sharedPrefHelper?.getDarkMode() ?: false
        binding.switchTheme.isChecked = isDarkMode

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                sharedPrefHelper?.saveDarkMode(isChecked)
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