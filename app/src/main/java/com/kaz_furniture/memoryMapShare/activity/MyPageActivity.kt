package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityMyPageBinding
import com.kaz_furniture.memoryMapShare.viewModel.MyPageViewModel

class MyPageActivity: BaseActivity() {
    private val viewModel: MyPageViewModel by viewModels()
    lateinit var binding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page)
        binding.lifecycleOwner = this
        viewModel.loadMarker()
        binding.userIdTextView.text = getString(R.string.userIdDisplay, myUser.userId)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadMarker()
        }
        binding.editProfileButton.setOnClickListener {
            ProfileEditActivity.start(this)
        }

        viewModel.markersList.observe(this, Observer {
            binding.markerListView.customAdapter.refresh(it)
            binding.swipeRefresh.isRefreshing = false
        })
        viewModel.imageViewClicked.observe(this, Observer {
            AlbumActivity.start(
                this,
                it.imageIdList,
                it.locationName,
                android.text.format.DateFormat.format(getString(R.string.date), it.memoryTime).toString())
        })
    }

    override fun onResume() {
        super.onResume()
        binding.userForIcon = myUser
        binding.userNameView.text = myUser.name
    }

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, MyPageActivity::class.java))
        }
    }

}