package com.irfannurrizki.panstoryapp.appuserinterface.appactivity

import android.Manifest
import android.content.Context
import androidx.activity.viewModels
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.irfannurrizki.panstoryapp.R
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppMainRepository
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiConfig
//import com.irfannurrizki.panstoryapp.apphelper.dataStore
import com.irfannurrizki.panstoryapp.appuserinterface.appcustomview.CustomLoadDialog
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppLoginViewModel
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppMainViewModel
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppMainViewModelFactory
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppViewModelFactory
import com.irfannurrizki.panstoryapp.databinding.ActivityAppAddStoryBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AppAddStoryActivity : AppCompatActivity() {
        private lateinit var binding: ActivityAppAddStoryBinding
        private lateinit var appLoginViewModel: AppLoginViewModel
        private val mainViewModel: AppMainViewModel by viewModels {
           AppMainViewModelFactory(this)
        }
        protected var customLoadDialog: CustomLoadDialog? = null
        private lateinit var currentPhotoPath: String
        private var getFile: File? = null
        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
        private lateinit var lastLocation: Location
        private lateinit var currentLatLng: LatLng
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityAppAddStoryBinding.inflate(layoutInflater)
            setContentView(binding.root)

            if (!allPermissionGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }

            setupViewModel()
            setupView()
            setupAction()
            getMyLocation()
        }

        private fun getMyLocation() {
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                    if (location != null) {
                        lastLocation = location
                        currentLatLng = LatLng(location.latitude, location.longitude)
                    }
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        private val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    getMyLocation()
                }
            }

        private fun setupView() {
            customLoadDialog = CustomLoadDialog(this)
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        }

        private fun setupAction() {
            binding.btCamerax.setOnClickListener {
               startCameraX()
            }

            binding.btGallery.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                val chooser = Intent.createChooser(intent, "Choose a Picture")
                launcherIntentGallery.launch(chooser)
            }

//            binding.switchLastLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
//                if (isChecked){
//                    binding.switchLastLocation.isChecked = true
//                    currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
//                } else {
//                    binding.switchLastLocation.isChecked = false
//                    currentLatLng = LatLng(0.0, 0.0)
//                }
//            }

            binding.btUpload.setOnClickListener {
                if (getFile != null) {
//                    if (!binding.switchLastLocation.isChecked) {
//                        currentLatLng = LatLng(0.0, 0.0)
//                    }
                    val file = reduceFileImage(getFile as File)

                    val description =
                        "${binding.edittextDescription.text}".toRequestBody("text/plain".toMediaType())
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )

                    currentLatLng = LatLng(0.0, 0.0)
                    appLoginViewModel.getToken().observe(this) { token ->
                        mainViewModel.uploadStory(
                            "Bearer $token",
                            imageMultipart,
                            description,
                            currentLatLng.latitude.toString()
                                .toRequestBody("text/plain".toMediaType()),
                            currentLatLng.longitude.toString()
                                .toRequestBody("text/plain".toMediaType())
                        ).observe(this) {
                            if (it != null) {
                                when (it) {
                                    is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Loading -> {
                                        showLoading()
                                    }
                                    is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Success<*> -> {
                                        hideLoading()
                                        Toast.makeText(
                                            this@AppAddStoryActivity,
                                            getString(R.string.upload_story_success),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }
                                     is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Error -> {
                                        hideLoading()
                                        Toast.makeText(
                                            this@AppAddStoryActivity,
                                            getString(R.string.upload_story_failed),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }

                } else {
                    Toast.makeText(
                        this@AppAddStoryActivity,
                        "Input Your Image First!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

            private fun setupViewModel() {
                appLoginViewModel = ViewModelProvider(
                    this,
                    AppViewModelFactory(
                        AppMainRepository.getInstance(dataStore, AppApiConfig.getAppApiService()
                        )
                    )
                )[AppLoginViewModel::class.java]
            }

            override fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<out String>,
                grantResults: IntArray
            ) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                if (requestCode == REQUEST_CODE_PERMISSIONS) {
                    if (!allPermissionGranted()) {
                        Toast.makeText(this, "Didn't get permission", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }

            private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(
                    baseContext,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }

            private val launcherIntentGallery = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    val selectedImg: Uri = result.data?.data as Uri

                    val myFile = uriToFile(selectedImg, this@AppAddStoryActivity)

                    getFile = myFile

                    binding.ivAddstorypreview.setImageURI(selectedImg)
                }
            }

            private val launcherIntentCameraX = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == CAMERA_X_RESULT) {
                    val myFile = it.data?.getSerializableExtra("picture") as File
                    getFile = myFile

                    val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

                    myFile?.let { file ->
                        rotateFile(file, isBackCamera)
                        binding.ivAddstorypreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
                    }
                }
            }

            private val launcherIntentCamera = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == RESULT_OK) {
                    val myFile = File(currentPhotoPath)
                    getFile = myFile

                    val result = BitmapFactory.decodeFile(getFile?.path)
                    binding.ivAddstorypreview.setImageBitmap(result)
                }
            }

            fun getAddress(lat: Double, lon: Double): String {
                val geocoder = Geocoder(this)
                val list = geocoder.getFromLocation(lat, lon, 1)
                return list?.get(0)!!.getAddressLine(0)
            }

            private fun showLoading() {
                if (!customLoadDialog?.isShowing!!) {
                    customLoadDialog?.show()
                }
            }

            private fun hideLoading() {
                if (customLoadDialog?.isShowing!!) {
                    customLoadDialog?.dismiss()
                }
            }

            private fun startCameraX() {
                val intent = Intent(this, AppCameraActivity::class.java)
                launcherIntentCameraX.launch(intent)
            }

            companion object {
                const val CAMERA_X_RESULT = 200
                private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
                private const val REQUEST_CODE_PERMISSIONS = 6
            }
        }