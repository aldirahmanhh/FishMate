package com.bangkit.fishmate.utils

import android.net.Uri
import com.bangkit.fishmate.ui.capture.CaptureActivity
import java.io.File

object FileUtils {

    // Fungsi untuk mendapatkan ukuran file dalam MB
    fun getFileSizeInMB(uri: Uri): Double {
        val file = File(uri.path!!)
        val sizeInBytes = file.length()
        return sizeInBytes.toDouble() / (1024 * 1024)  // Konversi ke MB
    }

    fun createImageFile(context: CaptureActivity): File {
        val timestamp = System.currentTimeMillis()
        val storageDir = context.cacheDir
        return File.createTempFile("tempImage_${timestamp}", ".jpg", storageDir)
    }
}
