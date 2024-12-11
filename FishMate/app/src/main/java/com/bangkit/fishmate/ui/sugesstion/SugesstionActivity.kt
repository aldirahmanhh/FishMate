package com.bangkit.fishmate.ui.sugesstion

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.fishmate.R

class SugesstionActivity : AppCompatActivity() {

    private lateinit var tindakanTextView: TextView
    private lateinit var pencegahanTextView: TextView
    private lateinit var gejalaTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sugesstion)

        tindakanTextView = findViewById(R.id.actionDesc)
        pencegahanTextView = findViewById(R.id.preventionDesc)
        gejalaTextView = findViewById(R.id.symptomDesc)

        val tindakan = intent.getStringExtra("Tindakan")
        val pencegahan = intent.getStringExtra("Pencegahan")
        val informasi = intent.getStringExtra("Informasi")

        Log.d("INi SUGEST", "tindakan = $tindakan, pencegahan = $pencegahan, informasi = $informasi")

        tindakanTextView.text = tindakan ?: "Tidak tersedia"
        pencegahanTextView.text = pencegahan ?: "Tidak tersedia"
        gejalaTextView.text = informasi ?: "Tidak tersedia"
    }
}
