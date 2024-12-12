package com.bangkit.fishmate.ui.capture

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.fishmate.utils.FileUtils

class CaptureViewModel : ViewModel() {

    private val _isFirstStart = MutableLiveData<Boolean>(true)
    val isFirstStart: LiveData<Boolean> get() = _isFirstStart

    fun setFirstStartShown() {
        _isFirstStart.value = false
    }
}
