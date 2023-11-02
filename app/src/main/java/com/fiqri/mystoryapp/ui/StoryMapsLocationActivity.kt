package com.fiqri.mystoryapp.ui

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.fiqri.mystoryapp.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.fiqri.mystoryapp.databinding.ActivityStoryMapsLocationBinding
import com.fiqri.mystoryapp.helper.login.LoginPreferences
import com.fiqri.mystoryapp.helper.login.LoginViewModel
import com.fiqri.mystoryapp.helper.login.LoginViewModelFactory
import com.fiqri.mystoryapp.helper.login.dataStore
import com.fiqri.mystoryapp.helper.stories.StoryViewModel
import com.fiqri.mystoryapp.helper.stories.ViewModelFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class StoryMapsLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapsLocationBinding
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapsLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle("Lokasi story")

        val pref = LoginPreferences.getInstance(application.dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(pref)).get(
            LoginViewModel::class.java
        )

        loginViewModel.getLoginInfo("password").observe(this) { password: String? ->
            if(password?.length!! < 8) {
                finish()
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val pref = LoginPreferences.getInstance(application.dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(pref)).get(
            LoginViewModel::class.java
        )

        val storyViewModel: StoryViewModel by viewModels {
            ViewModelFactory(this)
        }

        loginViewModel.getLoginInfo("token").observe(this) { utoken: String? ->
            storyViewModel.getStoryWithLocationList(utoken!!)

        }

        storyViewModel.listDataStory.observe(this) {listDataStory ->
            for (prop in listDataStory) {
                val storyLocation = LatLng(prop.lat.toString().toDouble(), prop.lon.toString().toDouble())
                mMap.addMarker(MarkerOptions().position(storyLocation).title("Lokasi story " + prop.name).snippet(prop.description))
                boundsBuilder.include(storyLocation)
            }

            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )

        }
        setMapStyle()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val pref = LoginPreferences.getInstance(application.dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(pref)).get(
            LoginViewModel::class.java
        )
        when(item.itemId) {
            R.id.logout -> {
                loginViewModel.logout()
                val toLogin = Intent(this@StoryMapsLocationActivity, LoginActivity::class.java)
                startActivity(toLogin)
                finish()
            }
        }
        return true
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "StoryMapsLocationActivity"
    }
}