package com.bangkit.fishmate.ui.result

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bangkit.fishmate.R
import com.bangkit.fishmate.data.ModelConfig
import com.bangkit.fishmate.databinding.ActivityResultBinding
import com.bangkit.fishmate.ViewModelFactory
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.BarChart
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val resultViewModel: ResultViewModel by viewModels()

    private lateinit var resultImageView: ImageView
    private lateinit var diagnosisTextView: TextView
    private lateinit var suggestionTextView: TextView
    private lateinit var explanationTextView: TextView
    private lateinit var barChart: BarChart


    private var imageUri: String? = null

    private val geminiApiKey = "AIzaSyAAGjOeI5M85HL4MEmOI9LeEt6zXw319eo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Initialize views
        resultImageView = findViewById(R.id.resultImageView)
        diagnosisTextView = findViewById(R.id.diagnosisTextView)
        suggestionTextView = findViewById(R.id.suggestionTextView)
        explanationTextView = findViewById(R.id.explanationTextView)
        barChart = findViewById(R.id.barChart)

        // Pass the barChart to the ViewModel
        resultViewModel.setBarChart(barChart)

        imageUri = intent.getStringExtra("image_uri")
        Log.d("ResultActivity", "Received image URI: $imageUri")  // Log untuk memverifikasi URI

        // Pastikan tidak null sebelum diproses lebih lanjut
        imageUri?.let { uri ->
            Glide.with(this).load(uri).into(resultImageView)

            // Convert URI to File and upload
            val file = File(getRealPathFromURI(Uri.parse(uri)))
            file.takeIf { it.exists() }?.let {
                uploadImage(it)
            } ?: run {
                Toast.makeText(this, "Invalid image file", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No image URI received", Toast.LENGTH_SHORT).show()
        }


        resultViewModel.suggestion.observe(this) { suggestion ->
            suggestionTextView.text = suggestion
        }
    }


    private fun uploadImage(file: File) {
        val requestBody = RequestBody.create("image/*".toMediaType(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)

        // Call API using coroutine
        lifecycleScope.launch {
            try {
                val response = ModelConfig.apiService.uploadImage(body)
                if (response.isSuccessful) {
                    val diagnosisResponse = response.body()
                    Log.d("ResultActivity", "Diagnosis Response: $diagnosisResponse")
                    diagnosisResponse?.let {
                        diagnosisTextView.text = "Diagnosis: ${it.diagnosis.label}"
                        explanationTextView.text = "Explanation: ${it.diagnosis.explanation}"
                        resultViewModel.getSuggestion(it.diagnosis.label)
                        resultViewModel.displayChart(it.modelOutput)
                    }
                } else {
                    Log.e("ResultActivity", "Upload failed: ${response.code()}")
                    Toast.makeText(this@ResultActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ResultActivity", "Error uploading image: ${e.message}", e)
                Toast.makeText(this@ResultActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }


    }



    private fun getRealPathFromURI(uri: Uri): String? {
        // Tentukan proyeksi kolom yang ingin Anda ambil
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)

        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            // Cek apakah kolom yang diperlukan ada
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)

            if (columnIndex != -1 && it.moveToFirst()) {
                return it.getString(columnIndex) // Mengambil path file
            }
        }

        // Jika tidak ada path, coba ambil dengan cara lain
        if (uri.scheme == "content") {
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use {
                val tempFile = File.createTempFile("tempImage", ".jpg")
                tempFile.outputStream().use { fileOut ->
                    it.copyTo(fileOut)
                }
                return tempFile.absolutePath // Kembalikan path sementara
            }
        }

        return null
    }




}

