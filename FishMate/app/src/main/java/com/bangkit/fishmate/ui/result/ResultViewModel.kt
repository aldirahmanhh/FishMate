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

    fun getSuggestion(diagnosis: String) {
        val prompt = """
            Berikan saran untuk penyakit $diagnosis dengan struktur sebagai berikut:
            1. Tindakan yang segera harus dilakukan: [Jelaskan tindakan yang harus segera dilakukan secara rinci].
            2. Saran Pencegahan: [Berikan rekomendasi pencegahan agar penyakit ini tidak terjadi lagi].
            3. Informasi lainnya: [Sertakan informasi tambahan yang relevan dengan penyakit ini].
        """.trimIndent()

        viewModelScope.launch {
            try {
                Log.d("ResultViewModel", "Sending prompt to AI: $prompt")
                val generateContent = generativeModel.generateContent(prompt)
                var aiSuggestion = generateContent.text ?: "" // Pastikan tidak null

                // Bersihkan format bold
                aiSuggestion = cleanBoldFormatting(aiSuggestion)

                Log.d("ResultViewModel", "AI Suggestion: $aiSuggestion")

                // Extract categorized suggestions
                val formattedSuggestion = formatCategorizedSuggestion(aiSuggestion)
                Log.d("ResultViewModel", "Formatted Suggestion: $formattedSuggestion")

                // Update suggestion LiveData
                _suggestion.value = formattedSuggestion

            } catch (e: Exception) {
                Log.e("ResultViewModel", "Error generating suggestion: ${e.message}", e)
                _suggestion.value = "Gagal mengambil saran"
            }
        }
    }

    // Fungsi untuk membersihkan format bold
    private fun cleanBoldFormatting(text: String): String {
        return text.replace(Regex("\\*\\*"), "")
    }

    // Extract categorized suggestions from the AI response
    private fun formatCategorizedSuggestion(suggestionText: String): String {
        val tindakanRegex = Regex("1\\. Tindakan yang Segera Harus Dilakukan:(.*?)(?=2\\. Saran Pencegahan:|$)", RegexOption.DOT_MATCHES_ALL)
        val pencegahanRegex = Regex("2\\. Saran Pencegahan:(.*?)(?=3\\. Informasi Lainnya:|$)", RegexOption.DOT_MATCHES_ALL)
        val informasiRegex = Regex("3\\. Informasi Lainnya:(.*)", RegexOption.DOT_MATCHES_ALL)

        val tindakan = tindakanRegex.find(suggestionText)?.groupValues?.get(1)?.trim() ?: "Tidak tersedia"
        val pencegahan = pencegahanRegex.find(suggestionText)?.groupValues?.get(1)?.trim() ?: "Tidak tersedia"
        val informasi = informasiRegex.find(suggestionText)?.groupValues?.get(1)?.trim() ?: "Tidak tersedia"

        return """
        1. Tindakan yang segera harus dilakukan:
        $tindakan

        2. Saran Pencegahan:
        $pencegahan

        3. Informasi lainnya:
        $informasi
    """.trimIndent()
    }


    // Fungsi tambahan untuk memisahkan setiap poin
    fun extractPoint(suggestionText: String, pointNumber: Int): String {
        val regex = when (pointNumber) {
            1 -> Regex("\\*\\*1\\. Tindakan yang Segera Harus Dilakukan:\\*(.*?)\\*\\*2\\. Saran Pencegahan:\\*", RegexOption.DOT_MATCHES_ALL)
            2 -> Regex("\\*\\*2\\. Saran Pencegahan:\\*(.*?)\\*\\*3\\. Informasi Lainnya:\\*", RegexOption.DOT_MATCHES_ALL)
            3 -> Regex("\\*\\*3\\. Informasi Lainnya:\\*(.*)", RegexOption.DOT_MATCHES_ALL)
            else -> return "Tidak valid"
        }
        return regex.find(suggestionText)?.groupValues?.get(1)?.trim() ?: "Tidak tersedia"
    }
}
