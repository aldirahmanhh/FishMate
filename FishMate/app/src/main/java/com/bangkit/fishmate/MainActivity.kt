package com.bangkit.fishmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bangkit.fishmate.ui.login.LoginActivity
import com.bangkit.fishmate.data.SharedPrefHelper
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bangkit.fishmate.databinding.ActivityMainBinding
import com.bangkit.fishmate.ui.theme.ThemeManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var themeManager : ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        themeManager = ThemeManager(this)

        MainScope().launch {
            val isDarkMode = themeManager.themeMode.first()
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        setContentView(binding.root)

        sharedPrefHelper = SharedPrefHelper(this)

        val token = sharedPrefHelper.getToken()
        if (token == null) {
            // Token tidak ditemukan, arahkan ke login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            // User sudah login, lanjutkan ke halaman utama
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
            val navController = navHostFragment.navController

            val bottomNavigationView = binding.navView
            NavigationUI.setupWithNavController(bottomNavigationView, navController)
        }

    }
}
