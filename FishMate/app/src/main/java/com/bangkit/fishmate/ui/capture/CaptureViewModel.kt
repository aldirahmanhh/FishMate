package com.bangkit.fishmate.ui.capture

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bangkit.fishmate.utils.FileUtils

class CaptureViewModel : ViewModel() {

    // Fungsi untuk mengecek ukuran file gambar (dalam MB)
    fun isFileSizeValid(uri: Uri): Boolean {
        val fileSizeInMB = FileUtils.getFileSizeInMB(uri)
        return fileSizeInMB <= 5  // Memeriksa jika ukuran file <= 5MB
    }
}
