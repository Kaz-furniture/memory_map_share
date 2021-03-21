package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allUserList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityCreateGroupBinding
import com.kaz_furniture.memoryMapShare.viewModel.CreateGroupViewModel

class CreateGroupActivity: BaseActivity() {
    private val viewModel: CreateGroupViewModel by viewModels()
    lateinit var binding: ActivityCreateGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)
        binding.lifecycleOwner = this
        binding.groupNameInput = viewModel.groupNameInput
        binding.groupMemberView.customAdapter.refresh(allUserList.filter { myUser.followingUserIds.contains(it.userId) })
        binding.createButton.setOnClickListener {
            viewModel.createShareGroup()
        }

        viewModel.groupCreated.observe(this, Observer {
            finish()
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.createGroup)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, CreateGroupActivity::class.java))
        }
    }
}