package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allGroupList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allUserList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityEditGroupBinding
import com.kaz_furniture.memoryMapShare.viewModel.EditGroupViewModel

class EditGroupActivity: BaseActivity() {
    private val viewModel: EditGroupViewModel by viewModels()
    lateinit var binding: ActivityEditGroupBinding
    lateinit var dataStore: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_group)
        binding.lifecycleOwner = this
        binding.groupNameInput = viewModel.groupNameInput
        dataStore = getSharedPreferences("DataStore", MODE_PRIVATE)
        intent.getStringExtra(KEY_ID)?.also {
            if (it.isNotBlank() && it != myUser.userId) viewModel.groupId = it
            else viewModel.groupId = allGroupList.firstOrNull { value -> myUser.groupIds.contains(value.groupId) }?.groupId ?:return@also
        } ?:finish()
        intent.getStringExtra(KEY_NAME)?.also {
            if (it.isNotBlank()) {
                viewModel.groupNameInput.value = it
                binding.groupNameDisplay.text = it
            } else {
                val groupName = allGroupList.firstOrNull { value -> myUser.groupIds.contains(value.groupId) }?.groupName ?:return@also
                viewModel.groupNameInput.value = groupName
                binding.groupNameDisplay.text = groupName
            }
        } ?:finish()

        binding.groupMemberView.customAdapter.refresh(allUserList.filter { MemoryMapShareApplication.myUser.followingUserIds.contains(it.userId) })

        binding.groupNameDisplay.setOnClickListener {
            PopupMenu(this, it).also { popupMenu ->
                val myGroupList = MemoryMapShareApplication.allGroupList.filter { value -> MemoryMapShareApplication.myUser.groupIds.contains(value.groupId) }
//                popupMenu.menu.add(1,0,0, getString(R.string.privateText))
                myGroupList.forEachIndexed { index, group ->
                    popupMenu.menu.add(1, index, index, group.groupName)
                }
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    val selectedGroupId = myGroupList[menuItem.itemId].groupId
                    viewModel.groupId = selectedGroupId
                    binding.groupNameDisplay.text = savedGroupText(selectedGroupId)
                    viewModel.groupNameInput.value = savedGroupText(selectedGroupId)
                    saveGroupId(selectedGroupId)
                    setResult(
                        RESULT_OK,
                        Intent().apply {
                            putExtra(KEY_GROUP_ID, selectedGroupId)
                            putExtra(KEY_GROUP_NAME, savedGroupText(selectedGroupId))
                        }
                    )
                    binding.groupMemberView.customAdapter.refresh(allUserList.filter { value -> myUser.followingUserIds.contains(value.userId) })
                    return@setOnMenuItemClickListener true
                }
            }.show()
        }
    }

    private fun saveGroupId(groupId: String?) {
        val editor = dataStore.edit()
        editor.putString(KEY_GROUP, groupId)
        editor.apply()
    }

    companion object {
        private const val KEY_GROUP_ID = "key_group_id"
        private const val KEY_GROUP_NAME = "key_group_name"
        private const val KEY_GROUP = "key_group"
        private const val KEY_NAME = "key_name"
        private const val KEY_ID = "key_id"
        fun newIntent(activity: Activity, groupName: String, groupId: String): Intent {
            return Intent(activity, EditGroupActivity::class.java).apply {
                putExtra(KEY_NAME, groupName)
                putExtra(KEY_ID, groupId)
            }
        }
    }
}