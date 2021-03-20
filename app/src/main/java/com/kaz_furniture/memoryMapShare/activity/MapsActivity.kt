package com.kaz_furniture.memoryMapShare.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allGroupList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.adapter.MyInfoWindowAdapter
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityMapsBinding
import com.kaz_furniture.memoryMapShare.viewModel.MapsViewModel

class MapsActivity : BaseActivity(), OnMapReadyCallback {

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
            binding.fab.visibility = View.GONE
            binding.centerMarker.visibility = View.VISIBLE
            binding.okButton.visibility = View.VISIBLE
            binding.cancelButton.visibility = View.VISIBLE
        }
        binding.okButton.setOnClickListener {
//            viewModel.selectedLocation = map.cameraPosition.target
            binding.fab.visibility = View.VISIBLE
            binding.centerMarker.visibility = View.INVISIBLE
            binding.okButton.visibility = View.GONE
            binding.cancelButton.visibility = View.GONE
            launchCreateMarkerActivity()
            if (FirebaseAuth.getInstance().currentUser == null) launchLoginActivity()
        }
        binding.cancelButton.setOnClickListener {
            binding.fab.visibility = View.VISIBLE
            binding.centerMarker.visibility = View.INVISIBLE
            binding.okButton.visibility = View.GONE
            binding.cancelButton.visibility = View.GONE
        }

        binding.moreButton.setOnClickListener {
            PopupMenu(this, it).also { popupMenu ->
                popupMenu.menuInflater.inflate(R.menu.menu_appbar_more, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when(menuItem.itemId) {
                        R.id.myPage -> if (FirebaseAuth.getInstance().currentUser == null) launchLoginActivity() else MyPageActivity.start(this)
                        R.id.addFriend -> if (FirebaseAuth.getInstance().currentUser == null) launchLoginActivity() else FriendSearchActivity.start(this)
                        R.id.create_group -> if (FirebaseAuth.getInstance().currentUser == null) launchLoginActivity() else CreateGroupActivity.start(this)
                        R.id.setting -> return@setOnMenuItemClickListener true
                        R.id.logout -> {
                            FirebaseAuth.getInstance().signOut()
                            Toast.makeText(this, "ログアウトしました", Toast.LENGTH_SHORT).show()
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
            }.show()
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
        viewModel.groupGet.observe(this, Observer {
            val menuItems = allGroupList.filter { value -> myUser.groupIds.contains(value.groupId) }.map { it.groupName }
            binding.groupNameDisplay.text = menuItems[0]
        })
    }

    @SuppressLint("MissingPermission")
    private fun requestPermission() {
        if (!locationPermission(this)) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        } else map.isMyLocationEnabled = true
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                showPermissionDeniedDialog()
            if (locationPermission(this))
                map.isMyLocationEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser != null) viewModel.getAllUser()
        viewModel.getAllGroup()
        startLocationUpdate()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdate()
    }

    private fun launchLoginActivity() {
        LoginActivity.start(this)
    }

    private fun launchCreateMarkerActivity() {
        val intent = CreateMarkerActivity.newIntent(this, map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
        startActivityForResult(intent, REQUEST_CODE_CREATE)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        if (locationPermission(this)) {
            val locationRequest = createLocationRequest() ?: return
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else return
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
            title(R.string.location_permission_denied_dialog_title)
            message(R.string.location_permission_denied_dialog_message)
            positiveButton(R.string.openSetting) {
                startActivity(
                        Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                        )
                )
            }
            negativeButton(R.string.ok) {
                dismiss()
            }
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
        private const val REQUEST_CODE_CREATE = 2000
    }
}