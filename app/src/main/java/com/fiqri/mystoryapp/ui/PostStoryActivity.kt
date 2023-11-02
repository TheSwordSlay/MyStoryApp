package com.fiqri.mystoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fiqri.mystoryapp.R
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fiqri.mystoryapp.data.remote.response.StoryPostResponse
import com.fiqri.mystoryapp.data.remote.retrofit.ApiConfig
import com.fiqri.mystoryapp.databinding.ActivityPostStoryBinding
import com.fiqri.mystoryapp.databinding.ActivityStoryListBinding
import com.fiqri.mystoryapp.helper.getImageUri
import com.fiqri.mystoryapp.helper.login.LoginPreferences
import com.fiqri.mystoryapp.helper.login.LoginViewModel
import com.fiqri.mystoryapp.helper.login.LoginViewModelFactory
import com.fiqri.mystoryapp.helper.login.dataStore
import com.fiqri.mystoryapp.helper.reduceFileImage
import com.fiqri.mystoryapp.helper.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class PostStoryActivity : AppCompatActivity(), OnMapReadyCallback {
    private var currentImageUri: Uri? = null
    private lateinit var binding: ActivityPostStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var userLocation: Location? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    override fun onMapReady(p0: GoogleMap) {
        getMyLastLocation()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher2 =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun getMyLastLocation() {
        if  (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    userLocation = location
                    Toast.makeText(
                        this@PostStoryActivity,
                        "Location Found",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@PostStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.location.isChecked = false
                }
            }
        } else {
            binding.location.isChecked = false
            requestPermissionLauncher2.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showStartMarker(location: Location): Location {
        return location
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun uploadImage(deskripsi: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = deskripsi
            showLoading(true)
            binding.uploadButton.setEnabled(false)
            binding.uploadButton.text = "Uploading..."

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            var lat = userLocation?.latitude.toString().toRequestBody("text/plain".toMediaType())
            var lon = userLocation?.longitude.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            if (userLocation != null) {
                lifecycleScope.launch {
                    try {
                        val apiService = ApiConfig.getApiService(intent.getStringExtra("token")!!)
                        val successResponse = apiService.uploadStoryWithLocation(multipartBody, requestBody, lat, lon)
                        showToast(successResponse.message!!)
                        showLoading(false)
                        val toList = Intent(this@PostStoryActivity, StoryListActivity::class.java)
                        toList.putExtra("reload", "yes")
                        startActivity(toList)
                        finish()
                    } catch (e: HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, StoryPostResponse::class.java)
                        showToast(errorResponse.message!!)
                        showLoading(false)
                        binding.uploadButton.setEnabled(true)
                        binding.uploadButton.text = "Upload"

                    }
                }
            } else {
                lifecycleScope.launch {
                    try {
                        val apiService = ApiConfig.getApiService(intent.getStringExtra("token")!!)
                        val successResponse = apiService.uploadStory(multipartBody, requestBody)
                        showToast(successResponse.message!!)
                        showLoading(false)
                        val toList = Intent(this@PostStoryActivity, StoryListActivity::class.java)
                        toList.putExtra("reload", "yes")
                        startActivity(toList)
                        finish()
                    } catch (e: HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, StoryPostResponse::class.java)
                        showToast(errorResponse.message!!)
                        showLoading(false)
                        binding.uploadButton.setEnabled(true)
                        binding.uploadButton.text = "Upload"
                    }
                }
            }

        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle("Add Post")
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener{
            startGallery()
        }

        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.uploadButton.setOnClickListener {
            uploadImage(binding.description.text.toString())
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.location.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                getMyLastLocation()
            } else {
                userLocation = null
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImage.setImageURI(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val pref = LoginPreferences.getInstance(application.dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(pref)).get(
            LoginViewModel::class.java
        )
        when(item.itemId) {
            R.id.logout -> {
                loginViewModel.logout()
                val toLogin = Intent(this@PostStoryActivity, LoginActivity::class.java)
                startActivity(toLogin)
                finish()
            }

            R.id.map -> {
                val toMap = Intent(this@PostStoryActivity, StoryMapsLocationActivity::class.java)
                startActivity(toMap)
                finish()
            }
        }
        return true
    }



    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }


}