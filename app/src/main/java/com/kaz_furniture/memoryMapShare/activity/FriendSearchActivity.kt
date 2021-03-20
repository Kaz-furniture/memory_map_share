package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityFriendSearchBinding
import com.kaz_furniture.memoryMapShare.viewModel.FriendSearchViewModel

class FriendSearchActivity: BaseActivity() {
    private val viewModel: FriendSearchViewModel by viewModels()
    lateinit var binding: ActivityFriendSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friend_search)
        binding.lifecycleOwner = this
        binding.searchInput = viewModel.inputForSearch
        binding.widgetSearch.setOnClickListener {
            viewModel.searchUser()
        }

        viewModel.searchedUsersList.observe(this, Observer {
            binding.searchedUsersView.customAdapter.refresh(it)
        })
    }

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, FriendSearchActivity::class.java))
        }
    }
}