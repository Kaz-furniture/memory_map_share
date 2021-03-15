package com.kaz_furniture.memoryMapShare.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityCreateMarkerBinding
import com.kaz_furniture.memoryMapShare.viewModel.CreateMarkerViewModel
import timber.log.Timber

class CreateMarkerActivity: BaseActivity() {
    private val viewModel: CreateMarkerViewModel by viewModels()
    lateinit var binding: ActivityCreateMarkerBinding
    private val uriList = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_marker)
        binding.lifecycleOwner = this
        binding.selectedImageView.customAdapter.refresh(listOf())
        binding.selectImageButton.setOnClickListener {
            launchAlbumActivity()
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
        fun newIntent(context: Context): Intent {
            return Intent(context, CreateMarkerActivity::class.java)
        }
    }
}