package com.bangkit.fishmate.ui.news

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.fishmate.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NewsPageActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_page)

        val webView: WebView = findViewById(R.id.webView)
        val url = intent.getStringExtra("url")

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        url?.let {
            webView.loadUrl(it)
        }

        val fabBack: FloatingActionButton = findViewById(R.id.fab_back)
        fabBack.setOnClickListener {
            finish()
        }

    }
}