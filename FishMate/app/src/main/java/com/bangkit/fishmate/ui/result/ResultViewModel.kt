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
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.launch

class ResultViewModel: ViewModel() {
    private var barChart: BarChart? = null
    private val _suggestion = MutableLiveData<String>()
    val suggestion: LiveData<String> get() = _suggestion

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
        val labels = listOf("Class 1", "Class 2", "Class 3")

        // Add confidence values to the bar chart
        modelOutput[0].forEachIndexed { index, value ->
            entries.add(BarEntry(index.toFloat(), value.toFloat()))
        }

        val dataSet = BarDataSet(entries, "Confidence Scores")
        val barData = BarData(dataSet)

        barChart?.data = barData
        barChart?.description?.isEnabled = false
        barChart?.xAxis?.valueFormatter = IndexAxisValueFormatter(labels)
        barChart?.xAxis?.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        barChart?.invalidate()
    }




    fun getSuggestion(diagnosis: String) {
        val prompt = "Berikan saran tindakan yang harus dilakukan jika ikan saya mengalami penyakit $diagnosis dalam 1 paragraf dengan penjelasan yang jelas dan terperinci."

        viewModelScope.launch {
            try {
                Log.d(this.toString(), "Generating suggestion for diagnosis: $diagnosis")
                val generateContent = generativeModel.generateContent(prompt)
                _suggestion.value = generateContent.text
            } catch (e: Exception) {
                Log.e("ResultViewModel", "Error generating suggestion: ${e.message}")
                _suggestion.value = "Failed to fetch suggestion"
            }
        }
    }

}