package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allUserList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.data.ShareGroup
import com.kaz_furniture.memoryMapShare.data.User
import com.kaz_furniture.memoryMapShare.view.GroupMemberSelectView
import timber.log.Timber

class CreateGroupViewModel: BaseViewModel() {
    val groupNameInput = MutableLiveData<String>()
    val userAndCheckedList = ArrayList<GroupMemberSelectView.Adapter.UserAndChecked>()
    val groupCreated = MutableLiveData<String>()

    fun createShareGroup() {
        val newGroup = ShareGroup().apply {
            groupName = groupNameInput.value ?:return
        }
        FirebaseFirestore.getInstance()
                .collection("groups")
                .document(newGroup.groupId)
                .set(newGroup)
                .addOnCompleteListener {
                    organizeUsers(newGroup.groupId)
                }
    }

    private fun organizeUsers(groupId: String) {
        userAndCheckedList.add(GroupMemberSelectView.Adapter.UserAndChecked().apply {
            userId = myUser.userId
            checked = true
        })
        val groupUsers = userAndCheckedList.filter { it.checked }.map { it.userId }
        groupUsers.forEach { userId ->
            val newUser = allUserList.firstOrNull { it.userId == userId }?.apply {
                val newList = ArrayList<String>().apply {
                    addAll(groupIds)
                    add(groupId)
                }
                groupIds = newList
                val newUsers = arrayListOf<String>().apply {
                    addAll(followingUserIds)
                    addAll(groupUsers.filterNot { followingUserIds.contains(it) || it == userId })
                }
                followingUserIds = newUsers
                Timber.d("followingList = $newUsers")
            } ?:return@forEach

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .set(newUser)
                    .addOnCompleteListener {
                        allUserList.apply {
                            removeAll { it.userId == userId }
                            add(newUser)
                        }
                        sendFcm(
                            newUser,
                            TYPE_CREATE_GROUP,
                            applicationContext.getString(R.string.channel_name_1),
                            applicationContext.getString(R.string.channel_content_1, myUser.name)
                        )
                        groupCreated.postValue(groupId)
                    }
        }
    }

    companion object {
        private const val TYPE_CREATE_GROUP = 0
    }

}