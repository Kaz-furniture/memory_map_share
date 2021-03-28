package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityAlbumBinding
import com.kaz_furniture.memoryMapShare.viewModel.AlbumViewModel

class AlbumActivity: BaseActivity() {
    private val viewModel: AlbumViewModel by viewModels()
    lateinit var binding: ActivityAlbumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_album)
        binding.lifecycleOwner = this
        val imageIdList = intent.getStringArrayListExtra(KEY_IDS)
        binding.albumListView.customAdapter.refresh(imageIdList ?: listOf())

    }

    companion object {
        private const val KEY_IDS = "key_image_ids"
        fun start(activity: Activity, imageIdList: ArrayList<String>) {
            activity.startActivity(Intent(activity, AlbumActivity::class.java).apply {
                putExtra(KEY_IDS, imageIdList)
            })
        }
    }
}