package com.bangkit.fishmate.ui.capture

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bangkit.fishmate.R
import com.bangkit.fishmate.ui.result.ResultActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CaptureActivity : AppCompatActivity() {

    private lateinit var cameraButton: Button
    private lateinit var galleryButton: Button
    private lateinit var analyzeButton: Button
    private lateinit var cancelButton: FloatingActionButton
    private lateinit var previewImageView: ImageView

    private lateinit var imageUri: Uri // To store image URI
    private lateinit var photoFile: File // To store the photo file

    private val GALLERY_REQUEST = 2
    private val CAMERA_REQUEST = 1
    private val STORAGE_PERMISSION_REQUEST_CODE = 1002
    private val CAMERA_PERMISSION_REQUEST_CODE = 1003

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)

        cameraButton = findViewById(R.id.cameraButton)
        galleryButton = findViewById(R.id.galeryButton)
        analyzeButton = findViewById(R.id.analyzeButton)
        cancelButton = findViewById(R.id.cancelButton)
        previewImageView = findViewById(R.id.previewImage)

        if (checkStoragePermission()) {
            setupUI()
        } else {
            requestStoragePermission()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun setupUI() {
        galleryButton.setOnClickListener {
            openGallery()
        }

        cameraButton.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        analyzeButton.setOnClickListener {
            if (::imageUri.isInitialized) {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("image_uri", imageUri.toString())
                startActivity(intent)
                Toast.makeText(this, "Image ready for analysis", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select or capture an image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        imageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = externalCacheDir
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            photoFile = this
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        ActivityCompat.requestPermissions(this, arrayOf(permission), STORAGE_PERMISSION_REQUEST_CODE)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupUI()
                } else {
                    Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show()
                }
            }
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST -> {
                    data?.data?.let { uri ->
                        previewImageView.setImageURI(uri)
                        imageUri = uri
                    }
                }
                CAMERA_REQUEST -> {
                    previewImageView.setImageURI(imageUri)
                }
            }
        }
    }
}
