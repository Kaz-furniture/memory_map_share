package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.memoryMapShare.GlideApp
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityProfileEditBinding
import com.kaz_furniture.memoryMapShare.viewModel.ProfileEditViewModel
import com.yalantis.ucrop.UCrop
import java.io.File

class ProfileEditActivity: BaseActivity() {
    private val viewModel: ProfileEditViewModel by viewModels()
    lateinit var binding: ActivityProfileEditBinding
    var uCropSrcUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        binding.lifecycleOwner = this
        binding.name = viewModel.userNameInput
        binding.profileImageSelect.setOnClickListener {
            selectImage()
        }
        binding.userForIcon = myUser
        binding.saveButton.setOnClickListener {
            if (uCropSrcUri != null) {
                viewModel.uploadIconImage(uCropSrcUri ?:return@setOnClickListener)
            }
            viewModel.uploadMyUser(uCropSrcUri == null)
        }
        viewModel.imageUploadFinished.observe(this, Observer {
            finish()
        })
        title = getString(R.string.profileEdit2)
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*")
        startActivityForResult(intent, RC_CHOOSE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) return
        else
            when (requestCode) {
                RC_CHOOSE_IMAGE -> {
                    data.data?.also {
                        uCropSrcUri = it
                        startUCrop()
                    }
                }
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data)
                    uCropSrcUri = resultUri
                    GlideApp.with(this).load(resultUri).circleCrop()
                            .placeholder(R.drawable.loading_image)
                            .into(binding.roundedImageView)
                }

                UCrop.RESULT_ERROR -> {
                    Toast.makeText(this, "取得失敗", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun startUCrop() {
        val file = File.createTempFile("${System.currentTimeMillis()}", ".temp", cacheDir)
        uCropSrcUri?.apply {
            UCrop.of(this, file.toUri())
                    .withAspectRatio(1f, 1f)
                    .withOptions(UCrop.Options().apply {
                        setToolbarTitle("画像トリミング")
                        setCompressionFormat(Bitmap.CompressFormat.JPEG)
//                        setCompressionQuality(75)
                        setHideBottomControls(true)
                        setCircleDimmedLayer(true)
                        setShowCropGrid(false)
                        setShowCropFrame(false)
                    })
                    .start(this@ProfileEditActivity)
        }
    }


    companion object {
        private const val RC_CHOOSE_IMAGE = 2001
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, ProfileEditActivity::class.java))
        }
    }
}