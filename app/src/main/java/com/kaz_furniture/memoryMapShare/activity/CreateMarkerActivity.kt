package com.kaz_furniture.memoryMapShare.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityCreateMarkerBinding
import com.kaz_furniture.memoryMapShare.viewModel.CreateMarkerViewModel
import java.util.*
import kotlin.collections.ArrayList

class CreateMarkerActivity: BaseActivity() {
    private val viewModel: CreateMarkerViewModel by viewModels()
    lateinit var binding: ActivityCreateMarkerBinding
    private val uriList = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_marker)
        binding.lifecycleOwner = this
        viewModel.latitude = intent.getDoubleExtra(KEY_LATITUDE, DEFAULT_LATITUDE)
        viewModel.longitude = intent.getDoubleExtra(KEY_LONGITUDE, DEFAULT_LONGITUDE)
        binding.selectedImageView.customAdapter.refresh(listOf())
        binding.timeDateDisplay.text = android.text.format.DateFormat.format(getString(R.string.date), Date())
        binding.selectImageButton.setOnClickListener {
            launchAlbumActivity()
        }
        binding.dateSelectButton.setOnClickListener {
            launchDateSelectDialog()
        }
        binding.submitButton.setOnClickListener {
            viewModel.imageUpload(uriList)
        }
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