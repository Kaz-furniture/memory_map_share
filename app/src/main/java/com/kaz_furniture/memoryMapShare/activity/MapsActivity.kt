package com.kaz_furniture.memoryMapShare.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allGroupList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allMarkerList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.adapter.MyInfoWindowAdapter
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.data.MyMarker
import com.kaz_furniture.memoryMapShare.data.User
import com.kaz_furniture.memoryMapShare.databinding.ActivityMapsBinding
import com.kaz_furniture.memoryMapShare.databinding.DialogDeleteConfirmBinding
import com.kaz_furniture.memoryMapShare.viewModel.MapsViewModel
import okhttp3.*
import timber.log.Timber

class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val viewModel: MapsViewModel by viewModels()
    lateinit var binding: ActivityMapsBinding
    var currentLatLng: LatLng = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    lateinit var dataStore: SharedPreferences

    private val registerForCreateMarker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result?.resultCode != RESULT_OK) return@registerForActivityResult
    }
    private val registerForCreateGroup = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_FIRST_USER) {
            saveGroupId(null)
        } else if (result.resultCode == RESULT_OK && FirebaseAuth.getInstance().currentUser != null) {
            val newGroupId = result.data?.getStringExtra(KEY_GROUP_ID)
            val newGroupName = result.data?.getStringExtra(KEY_GROUP_NAME)
            binding.groupNameDisplay.text = newGroupName
            saveGroupId(newGroupId)
            viewModel.getAllGroup()
            map.clear()
        } else return@registerForActivityResult
    }
    private val registerForLogin = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result?.resultCode != RESULT_OK) return@registerForActivityResult
        if (FirebaseAuth.getInstance().currentUser != null) {
            viewModel.getAllUser()
            viewModel.getAllGroup()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps)
        binding.lifecycleOwner = this
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        dataStore = getSharedPreferences("DataStore", MODE_PRIVATE)

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

        binding.groupNameDisplay.setOnClickListener {
            PopupMenu(this, it).also { popupMenu ->
                val myGroupList = allGroupList.filter { value -> myUser.groupIds.contains(value.groupId) && value.deletedAt == null }
                popupMenu.menu.add(1,0,0, getString(R.string.privateText))
                myGroupList.forEachIndexed { index, group ->
                    popupMenu.menu.add(1, index + 1, index + 1, group.groupName)
                }
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    val selectedGroupId = if (menuItem.itemId != 0) myGroupList[menuItem.itemId - 1].groupId else null
                    binding.groupNameDisplay.text = savedGroupText(selectedGroupId)
                    saveGroupId(selectedGroupId)
                    initMark(selectedGroupId)
                    return@setOnMenuItemClickListener true
                }
            }.show()
        }

        binding.moreButton.setOnClickListener {
            PopupMenu(this, it).also { popupMenu ->
                popupMenu.menuInflater.inflate(R.menu.menu_appbar_more, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when(menuItem.itemId) {
                        R.id.myPage -> if (FirebaseAuth.getInstance().currentUser == null) launchLoginActivity() else MyPageActivity.start(this)
                        R.id.addFriend -> if (FirebaseAuth.getInstance().currentUser == null) launchLoginActivity() else FriendSearchActivity.start(this)
                        R.id.create_group -> if (FirebaseAuth.getInstance().currentUser == null) launchLoginActivity() else launchCreateGroupActivity()
                        R.id.edit_group -> editGroupSelected()
//                        R.id.setting -> return@setOnMenuItemClickListener true
                        R.id.logout -> showConfirmDialog()
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
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    updateCount++
//                    if (updateCount == 1) map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM_LEVEL))
//                    binding.locationText.text = getString(R.string.locationText, updateCount, location.longitude, location.latitude)
                }
            }
        }

        viewModel.markerFinished.observe(this, Observer {
            val savedGroupId = dataStore.getString(KEY_GROUP,"")
            binding.groupNameDisplay.text = savedGroupText(savedGroupId)
            initMark(dataStore.getString(KEY_GROUP,""))
        })
        if (FirebaseAuth.getInstance().currentUser != null) {
            viewModel.getAllUser()
            viewModel.getAllGroup()
        } else binding.groupNameDisplay.text = getString(R.string.privateText)
    }

    private fun initMark(groupId: String?) {
        map.clear()
        Timber.d("groupId = $groupId")
        if (groupId.isNullOrBlank()) {
            for (value in allMarkerList.filter { it.groupId == myUser.userId }) {
                map.addMarker(MarkerOptions().position(LatLng(value.latLng.latitude, value.latLng.longitude))).apply {
                    tag = value
                }
            }
        } else {
            for (value in allMarkerList.filter { it.groupId == groupId }) {
                map.addMarker(MarkerOptions().position(LatLng(value.latLng.latitude, value.latLng.longitude))).apply {
                    tag = value
                }
            }
        }
    }

    private fun showConfirmDialog() {
        MaterialDialog(this).show {
            title = getString(R.string.logoutConfirm)
            val bindingDialog = DialogDeleteConfirmBinding.inflate(LayoutInflater.from(this@MapsActivity), null, false)
            bindingDialog.apply {
                titleTextView.text = getString(R.string.logoutConfirm)
                yesButton.setOnClickListener {
                    dismiss()
                    FirebaseAuth.getInstance().signOut()
                    myUser = User()
                    binding.groupNameDisplay.text = getString(R.string.privateText)
                    map.clear()
                    Toast.makeText(this@MapsActivity, "ログアウトしました", Toast.LENGTH_SHORT).show()
                }
                cancelButton.setOnClickListener {
                    dismiss()
                }
            }
            setContentView(bindingDialog.root)
        }
    }

    private fun editGroupSelected() {
        if (allGroupList.none { value -> myUser.groupIds.contains(value.groupId) && value.deletedAt == null }) {
            Toast.makeText(this, "グループがありません", Toast.LENGTH_SHORT).show()
            return
        }
        if (FirebaseAuth.getInstance().currentUser == null) launchLoginActivity() else launchEditGroupActivity()
    }

    private fun saveGroupId(groupId: String?) {
        val editor = dataStore.edit()
        editor.putString(KEY_GROUP, groupId)
        editor.apply()
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
        if (FirebaseAuth.getInstance().currentUser != null) viewModel.getAllMarker()
        startLocationUpdate()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdate()
    }

    private fun launchLoginActivity() {
        val intent = LoginActivity.newIntent(this)
        registerForLogin.launch(intent)
    }

    private fun launchEditGroupActivity() {
        val groupId = dataStore.getString(KEY_GROUP,"") ?:""
        val groupName = allGroupList.firstOrNull { it.groupId == groupId }?.groupName ?:""
        val intent = EditGroupActivity.newIntent(this, groupName, groupId)
        registerForCreateGroup.launch(intent)
    }

    private fun launchCreateGroupActivity() {
        val intent = CreateGroupActivity.newIntent(this)
        registerForCreateGroup.launch(intent)
    }

    private fun launchCreateMarkerActivity() {
        val intent = CreateMarkerActivity.newIntent(this, map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
        registerForCreateMarker.launch(intent)
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
            interval = 30000
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
//        val place = LatLng(35.6598, 139.7024)
//        map.addMarker(MarkerOptions().position(place).title("this is marker!"))
        map.setInfoWindowAdapter(MyInfoWindowAdapter(this))
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM_LEVEL))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE), DEFAULT_ZOOM_LEVEL))
        map.setOnInfoWindowClickListener {
            val myMarker = it.tag as MyMarker? ?: return@setOnInfoWindowClickListener
            AlbumActivity.start(
                this,
                myMarker.imageIdList,
                myMarker.locationName,
                android.text.format.DateFormat.format(getString(R.string.date), myMarker.memoryTime).toString(),
                myMarker.memo,
                myMarker.markerId
            )
        }
    }


    companion object {
        private const val KEY_GROUP_ID = "key_group_id"
        private const val KEY_GROUP_NAME = "key_group_name"
        private const val KEY_GROUP = "key_group_pro"
        private const val DEFAULT_ZOOM_LEVEL = 8F
        private const val DEFAULT_LATITUDE = 35.6598
        private const val DEFAULT_LONGITUDE = 139.7024
        private const val PERMISSION_REQUEST_CODE = 1000
    }
}