package com.irfannurrizki.panstoryapp.appuserinterface.appactivity.mapactivity

import android.Manifest
import androidx.activity.viewModels
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.ThemedSpinnerAdapter
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.*
import com.irfannurrizki.panstoryapp.R
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppMainRepository
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiConfig
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppLoginViewModel
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppMapViewModel
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppViewModelFactory
import com.irfannurrizki.panstoryapp.databinding.ActivityAppMapBinding
import com.irfannurrizki.panstoryapp.apphelper.AppHelper
import com.irfannurrizki.panstoryapp.appuserinterface.appactivity.AppMainActivity
import com.irfannurrizki.panstoryapp.appuserinterface.appcustomview.CustomLoadDialog

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppMapActivity : AppCompatActivity(),OnMapReadyCallback {
    private lateinit var binding: ActivityAppMapBinding
    private lateinit var appMapViewModel: AppMapViewModel
    private val appLoginViewModel: AppLoginViewModel by viewModels {
        AppViewModelFactory(
            AppMainRepository.getInstance(
                dataStore,
                AppApiConfig.getAppApiService()
            )
        )
    }
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var maps: GoogleMap
    private var customLoadDialog: CustomLoadDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
    }

    private fun setupViewModel() {
        customLoadDialog = CustomLoadDialog(this)
        appMapViewModel = ViewModelProvider(
            this,
            AppViewModelFactory(
                AppMainRepository.getInstance(
                    dataStore,
                    AppApiConfig.getAppApiService()
                )
            )
        )[AppMapViewModel::class.java]
    }

    private fun setupView() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mapview_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type_map -> {
                maps.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type_map -> {
                maps.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type_map -> {
                maps.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type_map -> {
                maps.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        maps = googleMap
        maps.uiSettings.isZoomControlsEnabled = true
        maps.uiSettings.isIndoorLevelPickerEnabled = true
        maps.uiSettings.isCompassEnabled = true
        maps.uiSettings.isMapToolbarEnabled = true

        maps.setOnPoiClickListener { poi ->
            val pMarker = maps.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            )
            pMarker?.showInfoWindow()
        }

        appLoginViewModel.getToken().observe(this) { token ->
            appMapViewModel.getLocationStoriesMap("Bearer ${token}").observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Loading -> {
                            showLoading()
                        }
                        is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Success -> {
                            hideLoading()
                            result.data.listStory.map {
                                if (it.lat != null && it.lon != null) {
                                    val location = LatLng(
                                        it.lat!!.toDouble(),
                                        it.lon!!.toDouble()
                                    )
                                    maps.addMarker(
                                        MarkerOptions().position(location)
                                            .title(it.name)
                                            .snippet(it.description)
                                            .icon(
                                                AppHelper.vectorToBitmap(
                                                    this,
                                                    R.drawable.ic_location_logo
                                                )
                                            )
                                    )
                                }
                            }
                        }
                        is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Error -> {
                            hideLoading()
                            Toast.makeText(this@AppMapActivity, result.error, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            getMyLocation()
        }
    }


    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            maps.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    maps.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
}
