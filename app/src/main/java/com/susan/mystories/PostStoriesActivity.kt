package com.susan.mystories

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.susan.mystories.databinding.ActivityPostStoriesBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.random.Random

@AndroidEntryPoint
class PostStoriesActivity : AppCompatActivity() {

    private lateinit var activityPostStoriesBinding: ActivityPostStoriesBinding
    private val accountModel by viewModels<StoriesViewModel>()
    private val storiesModel by viewModels<ViewStoriesViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPostStoriesBinding = ActivityPostStoriesBinding.inflate(layoutInflater)
        setContentView(activityPostStoriesBinding.root)
        supportActionBar?.title = getString(R.string.upload_story)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setupPermission()
        setNewStories()
        getStories()
        validateButton()
        storiesModel.loadAllData.observe(this) {
            showLoading(it)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    R.string.data_error,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupPermission(){
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation() {
        if (
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val addressName = getAlamat(LatLng(location.latitude, location.longitude))
                    activityPostStoriesBinding.etAddress.setText(addressName)
                } else {
                    Toast.makeText(this@PostStoriesActivity, R.string.data_error, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getAlamat(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(this)
            val allAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (allAddress.isEmpty()) getString(R.string.data_error) else allAddress[0].getAddressLine(
                0
            )
        } catch (e: Exception) {
            getString(R.string.data_error)
        }
    }

    private fun spesifikAlamat(locationName: String): LatLng {
        return try {
            val randomLatitude = randomCoordinate()
            val randomLongitude = randomCoordinate()

            val geocoder = Geocoder(this)
            val allLocation = geocoder.getFromLocationName(locationName, 1)
            if (allLocation.isEmpty()) {
                LatLng(randomLatitude, randomLongitude)
            } else {
                LatLng(allLocation[0].latitude, allLocation[0].longitude)
            }
        } catch (e: Exception) {
            LatLng(0.0, 0.0)
        }
    }

    private fun randomCoordinate(): Double {
        return Random.nextDouble(15.0, 100.0)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> getMyLocation()
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> getMyLocation()
            else -> {}
        }
    }

    private fun getStories() {
        accountModel.getAccountStories().observe(this) {
            if (it.token.trim() != "") {
                ACCOUNT = it.token
            }
        }
    }

    private fun setNewStories() {
        if (getFile == null) {
            Glide.with(this).load(getFile).placeholder(R.drawable.ic_baseline_image_24)
                .fallback(R.drawable.ic_baseline_image_24).into(activityPostStoriesBinding.image)
        }
    }

    private fun startCameraX(){
        val intentCamera = Intent(this, OpenCameraActivity::class.java)
        launcherCamera.launch(intentCamera)
    }

    private val launcherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Camera_X_RESULT_CODE){
            val fileCamera = it.data?.getSerializableExtra("picture") as File
            val isBackCamera= it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = fileCamera
            val result = rotateBitmapImage( BitmapFactory.decodeFile(getFile?.path), isBackCamera)
            activityPostStoriesBinding.image.setImageBitmap(result)
        }
    }

    private fun addImage(){
        val intentImage = Intent()
        intentImage.action = Intent.ACTION_GET_CONTENT
        intentImage.type = "image/*"
        val chooserPhoto = Intent.createChooser(intentImage,"Choose a Picture")
        launcherGallery.launch(chooserPhoto)
    }

    private var getFile: File? = null
    private val launcherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == RESULT_OK){
            val selectedImage : Uri = result.data?.data as Uri
            val fileImage = uriToFile(selectedImage, this@PostStoriesActivity)
            getFile = fileImage

            activityPostStoriesBinding.image.setImageURI(selectedImage)
        }
    }

    private fun postStories() {
        val description = activityPostStoriesBinding.etDesc.text.toString()

        when {
            getFile == null -> {
                Toast.makeText(
                    this@PostStoriesActivity,
                    R.string.add_image,
                    Toast.LENGTH_SHORT
                ).show()
            }
            description.trim().isEmpty() -> {
                Toast.makeText(
                    this@PostStoriesActivity,
                    R.string.description,
                    Toast.LENGTH_SHORT
                ).show()
                activityPostStoriesBinding.etDesc.error = getString(R.string.add_desc)
            }
            else -> {
                val file = reduceFileImage(getFile as File)
                val address = activityPostStoriesBinding.etAddress.text.toString()
                val location = spesifikAlamat(address)
                storiesModel.postSatuStories(ACCOUNT, description, file, location)

                storiesModel.startPosting.observe(this) {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    if (!it.error) {
                        val intent = Intent(this@PostStoriesActivity, ViewStoriesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            activityPostStoriesBinding.progressBar.visibility = View.VISIBLE
        } else {
            activityPostStoriesBinding.progressBar.visibility = View.GONE
        }
    }

    private fun validateButton(){
        activityPostStoriesBinding.btnLoc.setOnClickListener { getMyLocation() }
        activityPostStoriesBinding.btnCam.setOnClickListener { startCameraX() }
        activityPostStoriesBinding.btnAlbum.setOnClickListener { addImage() }
        activityPostStoriesBinding.post.setOnClickListener { postStories() }
    }

    companion object{
        const val Camera_X_RESULT_CODE = 200
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
        private var ACCOUNT = "token"
    }
}