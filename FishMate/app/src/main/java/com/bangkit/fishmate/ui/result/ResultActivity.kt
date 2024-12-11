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
import androidx.lifecycle.lifecycleScope
import com.bangkit.fishmate.R
import com.bangkit.fishmate.data.ModelConfig
import com.bangkit.fishmate.data.Response.DetectionHistory
import com.bangkit.fishmate.data.SharedPrefHelper
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : AppCompatActivity() {

    private lateinit var resultImageView: ImageView
    private lateinit var diagnosisTextView: TextView
    private lateinit var suggestionTextView: TextView
    private lateinit var explanationTextView: TextView
    private lateinit var barChart: com.github.mikephil.charting.charts.BarChart

    private var imageUri: String? = null

    // ViewModel
    private val resultViewModel: ResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Initialize views
        resultImageView = findViewById(R.id.resultImageView)
        diagnosisTextView = findViewById(R.id.diagnosisTextView)
        suggestionTextView = findViewById(R.id.suggestionTextView)
        explanationTextView = findViewById(R.id.explanationTextView)
        barChart = findViewById(R.id.barChart) // Initialize BarChart

        imageUri = intent.getStringExtra("image_uri")
        Log.d("ResultActivity", "Received image URI: $imageUri")

        // Set up the BarChart
        resultViewModel.setBarChart(barChart)

        // Display image
        imageUri?.let {
            Glide.with(this).load(it).into(resultImageView)
            val file = getFileFromUri(Uri.parse(it))
            if (file.exists()) {
                uploadImage(file)
            } else {
                Toast.makeText(this, "Invalid image file", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No image URI received", Toast.LENGTH_SHORT).show()
        }

        // Observe suggestion from ViewModel
        resultViewModel.suggestion.observe(this) { suggestion ->
            suggestionTextView.text = suggestion
        }
    }

    private fun uploadImage(file: File) {
        val requestBody = okhttp3.RequestBody.create("image/*".toMediaType(), file)
        val body = okhttp3.MultipartBody.Part.createFormData("file", file.name, requestBody)

        lifecycleScope.launch {
            try {
                val response = ModelConfig.apiService.uploadImage(body)
                if (response.isSuccessful) {
                    val diagnosisResponse = response.body()
                    Log.d("ResultActivity", "Diagnosis Response: $diagnosisResponse")
                    diagnosisResponse?.let {
                        diagnosisTextView.text = "Diagnosis: ${it.diagnosis.label}"
                        explanationTextView.text = "Explanation: ${it.diagnosis.explanation}"
                        suggestionTextView.text = "Suggestion: ${it.diagnosis.suggestion}"

                        // Panggil ViewModel untuk menampilkan chart
                        resultViewModel.displayChart(it.modelOutput)

                        // Menampilkan saran dari model AI
                        resultViewModel.getSuggestion(it.diagnosis.label)

                        // Save to history
                        val dateDetected = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        val detectionHistory = DetectionHistory(
                            imageUri = file.absolutePath,
                            diagnosis = it.diagnosis.label,
                            explanation = it.diagnosis.explanation,
                            suggestion = it.diagnosis.suggestion,
                            dateDetected = dateDetected
                        )
                        saveToHistory(detectionHistory)
                    }
                } else {
                    Toast.makeText(this@ResultActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ResultActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ResultActivity", "Error: ${e.message}", e)
            }
        }
    }


    private fun saveToHistory(detectionHistory: DetectionHistory) {
        val sharedPrefHelper = SharedPrefHelper(this)

        // Get current history or empty list if not found
        val currentHistory = sharedPrefHelper.getHistory().toMutableList()
        currentHistory.add(detectionHistory)

        // Save updated history list
        sharedPrefHelper.saveHistory(currentHistory)
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}
