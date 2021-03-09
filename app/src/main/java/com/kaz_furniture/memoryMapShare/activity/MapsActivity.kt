package com.kaz_furniture.memoryMapShare.activity

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kaz_furniture.memoryMapShare.MyInfoWindowAdapter
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityMapsBinding
import com.kaz_furniture.memoryMapShare.viewModel.MapsViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val viewModel: MapsViewModel by viewModels()
    lateinit var binding: ActivityMapsBinding
    var currentLatLng: LatLng = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_maps)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps)
        binding.lifecycleOwner = this
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.fab.setOnClickListener {
            binding.horizon.visibility = View.VISIBLE
            binding.vertical.visibility = View.VISIBLE
            binding.okButton.visibility = View.VISIBLE
        }
        binding.okButton.setOnClickListener {
            Toast.makeText(this, "CLICKED", Toast.LENGTH_LONG).show()
            binding.horizon.visibility = View.INVISIBLE
            binding.vertical.visibility = View.INVISIBLE
            binding.okButton.visibility = View.GONE
        }

        var updateCount = 0
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?:return
                for (location in locationResult.locations) {
                    updateCount++
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    binding.locationText.text = getString(R.string.locationText, updateCount, location.longitude, location.latitude) //"[${updateCount}] ${location.latitude}, ${location.longitude}"
                }
            }
        }
    }

    private fun requestPermission() {
        val permissionAccessCoarseLocationApproved =
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!permissionAccessCoarseLocationApproved) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        } else map.isMyLocationEnabled = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                map.isMyLocationEnabled = true
            else
                showPermissionDeniedDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdate()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdate()
    }

    private fun startLocationUpdate() {
        val locationRequest = createLocationRequest() ?: return
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopLocationUpdate() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest(): LocationRequest? {
        return LocationRequest.create().apply {
            interval = 10000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    private fun showPermissionDeniedDialog() {
        MaterialDialog(this).show {
            title(res = R.string.location_permission_denied_dialog_title)
            message(res = R.string.location_permission_denied_dialog_message)
            positiveButton(res = R.string.ok)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        requestPermission()
        // Add a marker in Sydney and move the camera
        val place = LatLng(35.6598, 139.7024)
        map.addMarker(MarkerOptions().position(place).title("this is marker!"))
        map.setInfoWindowAdapter(MyInfoWindowAdapter(this))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM_LEVEL))
    }

    companion object {
        private const val DEFAULT_ZOOM_LEVEL = 14F
        private const val DEFAULT_LATITUDE = 35.6598
        private const val DEFAULT_LONGITUDE = 139.7024
        private const val PERMISSION_REQUEST_CODE = 1000
    }
}