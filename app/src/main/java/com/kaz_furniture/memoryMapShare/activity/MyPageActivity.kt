package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allGroupList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allMarkerList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityMyPageBinding
import com.kaz_furniture.memoryMapShare.viewModel.MyPageViewModel
import timber.log.Timber

class MyPageActivity: BaseActivity() {
    private val viewModel: MyPageViewModel by viewModels()
    lateinit var binding: ActivityMyPageBinding
    lateinit var dataStore: SharedPreferences
    private var selectedGroupId: String? = null
    private var isReversed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page)
        binding.lifecycleOwner = this
        binding.userIdTextView.text = getString(R.string.userIdDisplay, myUser.userId)
        dataStore = getSharedPreferences("DataStore", MODE_PRIVATE)
        val savedGroupId = dataStore.getString(KEY_GROUP,"")
        selectedGroupId = savedGroupId
        dataStore.getBoolean(KEY_IS_REVERSED, false).also {
            isReversed = it
        }
        binding.groupNameDisplay.text = savedGroupText(savedGroupId)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadMarker()
        }
        binding.editProfileButton.setOnClickListener {
            ProfileEditActivity.start(this)
        }
        binding.upDownButton.setOnClickListener {
            isReversed = !isReversed
            dataStore.edit().also {
                it.putBoolean(KEY_IS_REVERSED, isReversed)
                Timber.d("isReversed = $isReversed")
                it.apply()
            }
            refreshWithReverse()
        }
        binding.copyButton.setOnClickListener {
            val clipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager? ?: return@setOnClickListener
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", myUser.userId))
            Toast.makeText(this, R.string.clipboard, Toast.LENGTH_SHORT).show()
        }
        binding.groupNameDisplay.setOnClickListener {
            PopupMenu(this, it).also { popupMenu ->
                val myGroupList = allGroupList.filter { value -> myUser.groupIds.contains(value.groupId) && value.deletedAt == null}
                popupMenu.menu.add(1,0,0, getString(R.string.privateText))
                myGroupList.forEachIndexed { index, group ->
                    popupMenu.menu.add(1, index + 1, index + 1, group.groupName)
                }
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    selectedGroupId = if (menuItem.itemId != 0) myGroupList[menuItem.itemId - 1].groupId else null
                    binding.groupNameDisplay.text = allGroupList.firstOrNull { value -> value.groupId == selectedGroupId }?.groupName ?:getString(R.string.privateText)
                    isReversed = false
                    dataStore.edit().also { shared ->
                        shared.putBoolean(KEY_IS_REVERSED, isReversed)
                        shared.apply()
                    }
                    refreshWithReverse()
                    return@setOnMenuItemClickListener true
                }
            }.show()
        }

        viewModel.markersList.observe(this, Observer {
            refreshWithReverse()
            binding.swipeRefresh.isRefreshing = false
        })
        viewModel.imageViewClicked.observe(this, Observer {
            AlbumActivity.start(
                this,
                it.imageIdList,
                it.locationName,
                android.text.format.DateFormat.format(getString(R.string.date), it.memoryTime).toString(),
                it.memo,
                it.markerId
            )
        })
    }

    private fun refreshWithReverse() {
        if (isReversed) {
            if (!selectedGroupId.isNullOrBlank())
                binding.markerListView.customAdapter.refresh(allMarkerList.filter { value -> value.groupId == selectedGroupId }.asReversed())
            else
                binding.markerListView.customAdapter.refresh(allMarkerList.filter { value -> value.groupId == myUser.userId }.asReversed())
        } else {
            if (!selectedGroupId.isNullOrBlank())
                binding.markerListView.customAdapter.refresh(allMarkerList.filter { value -> value.groupId == selectedGroupId })
            else
                binding.markerListView.customAdapter.refresh(allMarkerList.filter { value -> value.groupId == myUser.userId })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMarker()
        binding.userForIcon = myUser
        binding.userNameView.text = myUser.name
    }

    companion object {
        private const val KEY_GROUP = "key_group"
        private const val KEY_IS_REVERSED = "key_is_reversed"
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, MyPageActivity::class.java))
        }
    }

}