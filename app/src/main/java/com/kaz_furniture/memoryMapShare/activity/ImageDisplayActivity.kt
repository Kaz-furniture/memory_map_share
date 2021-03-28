package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityImageDisplayBinding
import com.kaz_furniture.memoryMapShare.viewModel.ImageDisplayViewModel

class ImageDisplayActivity: BaseActivity() {
    private val viewModel: ImageDisplayViewModel by viewModels()
    lateinit var binding: ActivityImageDisplayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_display)
        intent.getStringExtra(KEY_ID)?.also {
            binding.imageId = it
        } ?: finish()
    }

    companion object {
        private const val KEY_ID = "key_id"
        fun start(activity: Activity, imageId: String) {
            activity.startActivity(Intent(activity, ImageDisplayActivity::class.java).apply {
                putExtra(KEY_ID, imageId)
            })
        }
    }
}