package com.bangkit.fishmate.ui.result

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.launch

class ResultViewModel : ViewModel() {
    private val _actionSuggestion = MutableLiveData<String>()
    val actionSuggestion: LiveData<String> get() = _actionSuggestion

    private val _preventionSuggestion = MutableLiveData<String>()
    val preventionSuggestion: LiveData<String> get() = _preventionSuggestion

    private val _symptomSuggestion = MutableLiveData<String>()
    val symptomSuggestion: LiveData<String> get() = _symptomSuggestion

    private var barChart: BarChart? = null
    private val _suggestions = MutableLiveData<Triple<String, String, String>>()
    val suggestions: LiveData<Triple<String, String, String>> = _suggestions

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-latest", // Your AI model name
        apiKey = com.bangkit.fishmate.BuildConfig.API_KEY,
        generationConfig = generationConfig {
            temperature = 0.7f
        }
    )

    fun setBarChart(chart: BarChart) {
        barChart = chart
    }

    fun displayChart(modelOutput: List<List<Double>>?) {
        if (barChart == null) {
            Log.e("ResultViewModel", "Bar chart is not initialized")
            return
        }
        if (modelOutput == null || modelOutput.isEmpty() || modelOutput[0].isEmpty()) {
            Log.e("ResultViewModel", "Invalid model output format or empty data")
            return
        }

        val entries = mutableListOf<BarEntry>()
        val labels = listOf("Normal", "Jamur", "Bakteri")

        // Tambahkan nilai confidence ke dalam BarChart
        modelOutput[0].forEachIndexed { index, value ->
            entries.add(BarEntry(index.toFloat(), value.toFloat()))
        }

        val dataSet = BarDataSet(entries, "Jenis Penyakit")
        val barData = BarData(dataSet)

        barChart?.data = barData
        barChart?.description?.isEnabled = false
        barChart?.xAxis?.valueFormatter = IndexAxisValueFormatter(labels)
        barChart?.xAxis?.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        barChart?.invalidate()
    }


    fun getSuggestionAction(diagnosis: String) {
        val prompt = """
           Tindakan yang segera harus dilakukan jika ikan mengalami $diagnosis, buat dalam 1 paragraf
        """.trimIndent()

        viewModelScope.launch {
            try {
                Log.d("ResultViewModel", "Sending prompt to AI: $prompt")
                val generateContent = generativeModel.generateContent(prompt)
                var aiSuggestion = generateContent.text ?: ""
                Log.d("ResultViewModel", "AI Suggestion: $aiSuggestion")
                aiSuggestion = cleanBoldFormatting(aiSuggestion)

                _actionSuggestion.postValue(aiSuggestion)  // Post the result to LiveData

            } catch (e: Exception) {
                Log.e("ResultViewModel", "Error generating suggestion: ${e.message}", e)
            }
        }
    }

    fun getSuggestionPrevention(diagnosis: String) {
        val prompt = """
          Pencegahan supaya ikan tidak mengalami $diagnosis, buat dalam 1 paragraf
        """.trimIndent()

        viewModelScope.launch {
            try {
                Log.d("ResultViewModel", "Sending prompt to AI: $prompt")
                val generateContent = generativeModel.generateContent(prompt)
                var aiSuggestion = generateContent.text ?: ""
                Log.d("ResultViewModel", "AI Suggestion: $aiSuggestion")
                aiSuggestion = cleanBoldFormatting(aiSuggestion)

                _preventionSuggestion.postValue(aiSuggestion)  // Post the result to LiveData

            } catch (e: Exception) {
                Log.e("ResultViewModel", "Error generating suggestion: ${e.message}", e)
            }
        }
    }

    fun getSuggestionSymptom(diagnosis: String) {
        val prompt = """
          Berikan ciri-ciri penyakit $diagnosis, buat dalam 1 paragraf
        """.trimIndent()

        viewModelScope.launch {
            try {
                Log.d("ResultViewModel", "Sending prompt to AI: $prompt")
                val generateContent = generativeModel.generateContent(prompt)
                var aiSuggestion = generateContent.text ?: ""
                Log.d("ResultViewModel", "AI Suggestion: $aiSuggestion")
                aiSuggestion = cleanBoldFormatting(aiSuggestion)

                _symptomSuggestion.postValue(aiSuggestion)  // Post the result to LiveData

            } catch (e: Exception) {
                Log.e("ResultViewModel", "Error generating suggestion: ${e.message}", e)
            }
        }
    }

    private fun cleanBoldFormatting(text: String): String {
        return text.replace(Regex("\\*\\*"), "")
    }


}
