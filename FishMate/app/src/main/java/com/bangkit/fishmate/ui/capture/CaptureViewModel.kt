package com.bangkit.fishmate.ui.capture

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bangkit.fishmate.utils.FileUtils

class CaptureViewModel : ViewModel() {

    fun isFileSizeValid(uri: Uri): Boolean {
        val fileSizeInMB = FileUtils.getFileSizeInMB(uri)
        return fileSizeInMB <= 5
    }
}
