package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityAlbumBinding
import com.kaz_furniture.memoryMapShare.fragment.ImageDisplayFragment
import com.kaz_furniture.memoryMapShare.viewModel.AlbumViewModel
import kotlin.collections.ArrayList

class AlbumActivity: BaseActivity() {
    private val viewModel: AlbumViewModel by viewModels()
    lateinit var binding: ActivityAlbumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_album)
        binding.lifecycleOwner = this
        intent.getStringArrayListExtra(KEY_IMAGES)?.also {
            binding.albumListView.customAdapter.refresh(it)
        }
        intent.getStringExtra(KEY_LOCATION_NAME)?.also {
            binding.titleView.text = it
        }
        intent.getStringExtra(KEY_DATE)?.also {
            binding.dateDisplay.text = it
        }
        intent.getStringExtra(KEY_MEMO)?.also {
            if (it.isBlank())
                binding.memoTextView.visibility = View.GONE
            else
                binding.memoTextView.text = it
        } ?: kotlin.run {
            binding.memoTextView.visibility = View.GONE
        }
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        viewModel.imageClickedLiveData.observe(this, androidx.lifecycle.Observer {
//            ImageDisplayActivity.start(this, it)
            val fragment = ImageDisplayFragment.newInstance(it)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, ImageDisplayFragment.tag)
                .addToBackStack(null)
                .commit()
        })
    }

    companion object {
        private const val KEY_MEMO = "key_memo"
        private const val KEY_IMAGES = "key_image_ids"
        private const val KEY_LOCATION_NAME = "key_location_name"
        private const val KEY_DATE = "key_date"
        fun start(activity: Activity, imageIdList: ArrayList<String>, locationName: String, memoryTime: String, memo: String) {
            activity.startActivity(Intent(activity, AlbumActivity::class.java).apply {
                putExtra(KEY_MEMO, memo)
                putExtra(KEY_IMAGES, imageIdList)
                putExtra(KEY_LOCATION_NAME, locationName)
                putExtra(KEY_DATE, memoryTime)
            })
        }
    }
}