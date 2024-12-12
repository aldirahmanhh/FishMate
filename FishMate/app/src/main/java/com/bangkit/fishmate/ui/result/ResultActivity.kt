package com.bangkit.fishmate.ui.result

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bangkit.fishmate.R
import com.bangkit.fishmate.data.ModelConfig
import com.bangkit.fishmate.data.Response.DetectionHistory
import com.bangkit.fishmate.data.SharedPrefHelper
import com.bangkit.fishmate.repository.HistoryRepository
import com.bangkit.fishmate.ui.HistoryActivity
import com.bangkit.fishmate.ui.capture.CaptureActivity
import com.bangkit.fishmate.ui.home.HomeFragment
import com.bangkit.fishmate.ui.sugesstion.SugesstionActivity
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
    private lateinit var explanationTextView: TextView
    private lateinit var barChart: com.github.mikephil.charting.charts.BarChart

    private var imageUri: String? = null
    private var diagnosisText: String? = null
    private var isUploadCompleted = false

    private val resultViewModel: ResultViewModel by viewModels()

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        progressBar = findViewById(R.id.Resloading)
        resultImageView = findViewById(R.id.resultImageView)
        diagnosisTextView = findViewById(R.id.diagnosisTextView)
        explanationTextView = findViewById(R.id.explanationTextView)
        barChart = findViewById(R.id.barChart) // Initialize BarChart


        imageUri = intent.getStringExtra("image_uri")
        Log.d("ResultActivity", "Received image URI: $imageUri")

        resultViewModel.setBarChart(barChart)

        imageUri?.let {
            Glide.with(this).load(it).into(resultImageView)
            val file = getFileFromUri(Uri.parse(it))
            if (file.exists()) {
                progressBar.visibility = View.VISIBLE
                uploadImage(file)
            } else {
                Toast.makeText(this, "Invalid image file", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No image URI received", Toast.LENGTH_SHORT).show()
        }
        suggestionButton()
    }

    private fun suggestionButton() {
        val getSuggestionButton: ImageButton = findViewById(R.id.getSuggestionButton)
        getSuggestionButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            if (isUploadCompleted && diagnosisText != null) {
                if (diagnosisText == "Normal Nile Fish") {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Tidak ada remedy khusus untuk ikan ini", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    resultViewModel.getSuggestionAction(diagnosisText!!)
                    resultViewModel.getSuggestionPrevention(diagnosisText!!)
                    resultViewModel.getSuggestionSymptom(diagnosisText!!)

                    resultViewModel.actionSuggestion.observe(this) { actionSuggestion ->
                        resultViewModel.preventionSuggestion.observe(this) { preventionSuggestion ->
                            resultViewModel.symptomSuggestion.observe(this) { symptomSuggestion ->
                                val intent = Intent(this, SugesstionActivity::class.java).apply {
                                    putExtra("Tindakan", actionSuggestion)
                                    putExtra("Pencegahan", preventionSuggestion)
                                    putExtra("Informasi", symptomSuggestion)
                                }
                                progressBar.visibility = View.GONE
                                startActivity(intent)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please upload an image first", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun uploadImage(file: File) {
        val requestBody = okhttp3.RequestBody.create("image/*".toMediaType(), file)
        val body = okhttp3.MultipartBody.Part.createFormData("file", file.name, requestBody)

        lifecycleScope.launch {
            try {
                val response = ModelConfig.apiService.uploadImage(body)
                if (response.isSuccessful) {
                    val diagnosisResponse = response.body()
                    Log.d("ResultActivity", "Diagnosis Response: $diagnosisResponse")
                    diagnosisResponse?.let {
                        val diagnosisText = it.diagnosis.label
                        val explanationText = it.diagnosis.explanation
                        val suggestionText = it.diagnosis.suggestion

                        this@ResultActivity.diagnosisText = diagnosisText
                        isUploadCompleted = true

                        diagnosisTextView.text = "Diagnosis: $diagnosisText"
                        explanationTextView.text = "Explanation: $explanationText"
                        resultViewModel.displayChart(it.modelOutput)

                        saveToHistory(diagnosisText, explanationText, suggestionText)
                        progressBar.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(
                        this@ResultActivity,
                        "Failed to upload image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ResultActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.e("ResultActivity", "Error: ${e.message}", e)
            }
        }
    }

    private fun saveToHistory(diagnosisText: String, explanationText: String, suggestionText: String) {
        val historyRepository = HistoryRepository(this)

        val dateDetected = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        val detectionHistory = DetectionHistory(
            imageUri = imageUri ?: "",
            diagnosis = diagnosisText,
            dateDetected = dateDetected
        )
        Log.d("ResultActivity", "Detection History: $detectionHistory")

        val currentHistory = historyRepository.getHistory().toMutableList()
        currentHistory.add(detectionHistory)

        // Menyimpan kembali seluruh riwayat
        historyRepository.saveHistory(currentHistory)

        Log.d("ResultActivity", "Saved to history: ${detectionHistory.imageUri}")
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

