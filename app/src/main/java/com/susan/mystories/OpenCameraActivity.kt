package com.susan.mystories

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.susan.mystories.databinding.ActivityOpenCameraBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OpenCameraActivity : AppCompatActivity() {

    private lateinit var activityOpenCamera: ActivityOpenCameraBinding
    private var takePhoto : ImageCapture? = null
    private var startCamera : CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityOpenCamera = ActivityOpenCameraBinding.inflate(layoutInflater)
        setContentView(activityOpenCamera.root)
        cameraValidate()
    }

    private fun startStoriesCamera() {
        val cameraProvider = ProcessCameraProvider.getInstance(this)
        cameraProvider.addListener({
            val providerCamera : ProcessCameraProvider = cameraProvider.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(activityOpenCamera.viewCamera.surfaceProvider)
                }
            takePhoto = ImageCapture.Builder().build()
            try {
                providerCamera.unbindAll()
                providerCamera.bindToLifecycle(
                    this,
                    startCamera,
                    preview,
                    takePhoto
                )
            } catch (e: Exception){
                Toast.makeText( this@OpenCameraActivity, getString(R.string.failed_cam), Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun cameraValidate(){
        activityOpenCamera.takePhoto.setOnClickListener{
            ambilPhoto()
        }
        activityOpenCamera.flipCamera.setOnClickListener{
            startCamera = if (startCamera == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA

            startStoriesCamera()
        }
    }

    private fun ambilPhoto(){
        val imageCaptureStory = takePhoto?:return
        val filePhotoStory = createFile(application)
        val outputOptionsStream = ImageCapture.OutputFileOptions.Builder(filePhotoStory).build()
        imageCaptureStory.takePicture(
            outputOptionsStream, ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intentCamera = Intent()
                    intentCamera.putExtra("picture", filePhotoStory)
                    intentCamera.putExtra("isBackCamera", startCamera == CameraSelector.DEFAULT_BACK_CAMERA)
                    setResult(PostStoriesActivity.Camera_X_RESULT_CODE, intentCamera)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@OpenCameraActivity, getString(R.string.failed_picture), Toast.LENGTH_SHORT).show()
                }

            }
        )
    }

    private fun hideStoriesSystem() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    public override fun onResume() {
        super.onResume()
        startStoriesCamera()
        hideStoriesSystem()
    }
}