package com.kaz_furniture.memoryMapShare.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityCreateMarkerBinding
import com.kaz_furniture.memoryMapShare.viewModel.CreateMarkerViewModel
import java.util.*
import kotlin.collections.ArrayList

class CreateMarkerActivity: BaseActivity() {
    private val viewModel: CreateMarkerViewModel by viewModels()
    lateinit var binding: ActivityCreateMarkerBinding
    private val uriList = ArrayList<Uri>()
    lateinit var dataStore: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_marker)
        binding.lifecycleOwner = this
        viewModel.latitude = intent.getDoubleExtra(KEY_LATITUDE, DEFAULT_LATITUDE)
        viewModel.longitude = intent.getDoubleExtra(KEY_LONGITUDE, DEFAULT_LONGITUDE)
        binding.selectedImageView.customAdapter.refresh(listOf())
        binding.timeDateDisplay.text = android.text.format.DateFormat.format(getString(R.string.date), Date())
        binding.locationName = viewModel.locationNameInput
        binding.memo = viewModel.memoInput

        dataStore = getSharedPreferences("DataStore", MODE_PRIVATE)
        val savedGroupId = dataStore.getString("KEY","")?.also {
            if (it.isNotBlank())
                viewModel.selectedGroupId = it
            else return@also
        }
        binding.groupNameDisplay.text = savedGroupText(savedGroupId)

        binding.selectImageButton.setOnClickListener {
            launchAlbumActivity()
        }
        binding.dateSelectButton.setOnClickListener {
            launchDateSelectDialog()
        }
        binding.submitButton.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser == null) launchLoginActivity()
            else viewModel.imageUpload(uriList)
        }
        binding.groupNameDisplay.setOnClickListener {
            PopupMenu(this, it).also { popupMenu ->
                val myGroupList = MemoryMapShareApplication.allGroupList.filter { value -> MemoryMapShareApplication.myUser.groupIds.contains(value.groupId) }
                popupMenu.menu.add(1,0,0, getString(R.string.privateText))
                myGroupList.forEachIndexed { index, group ->
                    popupMenu.menu.add(1, index + 1, index + 1, group.groupName)
                }
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    val selectedGroupId = if (menuItem.itemId != 0) myGroupList[menuItem.itemId - 1].groupId else null
                    viewModel.selectedGroupId = selectedGroupId
                    binding.groupNameDisplay.text = MemoryMapShareApplication.allGroupList.firstOrNull { value -> value.groupId == selectedGroupId }?.groupName ?:getString(R.string.privateText)
                    return@setOnMenuItemClickListener true
                }
            }.show()
        }

        viewModel.imageUploaded.observe(this, androidx.lifecycle.Observer {
            viewModel.imageUploadedInt(it)
        })
        viewModel.imageUploadFinished.observe(this, androidx.lifecycle.Observer {
            setResult(RESULT_OK)
            finish()
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.createMarker)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun launchLoginActivity() {
        LoginActivity.start(this)
    }

    private fun launchDateSelectDialog() {
        MaterialDialog(this).show {
            datePicker { _, date ->
                binding.timeDateDisplay.text = android.text.format.DateFormat.format(getString(R.string.date), date)
                viewModel.calendar = date
            }
        }
    }

    private fun launchAlbumActivity() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_OPEN_DOCUMENT
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
        }
        startActivityForResult(intent, REQUEST_CODE_ALBUM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ALBUM) {
            val itemCount = data?.clipData?.itemCount ?:0
            if (itemCount == 0) {
                val uri = data?.data
                uri?.let { uriList.add(it)}
            }
            for (i in 0 until itemCount) {
                val uri = data?.clipData?.getItemAt(i)?.uri
                uri?.let { uriList.add(it) }
            }
            binding.selectedImageView.customAdapter.refresh(uriList)
        }
    }

    companion object {
        private const val REQUEST_CODE_ALBUM = 1000
        private const val KEY_LATITUDE = "key latitude"
        private const val KEY_LONGITUDE = "key longitude"
        private const val DEFAULT_LATITUDE = 35.6598
        private const val DEFAULT_LONGITUDE = 139.7024
        fun newIntent(context: Context, latitude: Double, longitude: Double): Intent {
            return Intent(context, CreateMarkerActivity::class.java).apply {
                putExtra(KEY_LATITUDE, latitude)
                putExtra(KEY_LONGITUDE, longitude)
            }
        }
    }
}