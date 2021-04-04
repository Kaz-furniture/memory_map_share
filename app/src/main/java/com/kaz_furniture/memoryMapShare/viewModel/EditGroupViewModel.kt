package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allGroupList
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.data.ShareGroup
import com.kaz_furniture.memoryMapShare.view.GroupMemberEditView
import com.kaz_furniture.memoryMapShare.view.GroupMemberSelectView
import timber.log.Timber

class EditGroupViewModel: BaseViewModel() {
    var groupId = ""
    val groupNameInput = MutableLiveData<String>()
    val userAndCheckedList = ArrayList<GroupMemberEditView.Adapter.UserAndChecked>()
    val groupEdited = MutableLiveData<String>()

    fun editShareGroup() {
        val newGroup = allGroupList.firstOrNull { it.groupId == groupId }?:return
        newGroup.apply {
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
        Timber.d("checked = ${userAndCheckedList.map { it.userId }}, ${userAndCheckedList.map { it.checked }}")
        userAndCheckedList.add(GroupMemberEditView.Adapter.UserAndChecked().apply {
            userId = MemoryMapShareApplication.myUser.userId
            checked = true
        })
        val groupUsers = userAndCheckedList.filter { it.checked }.map { it.userId }
        groupUsers.forEach { userId ->
            val newUser = MemoryMapShareApplication.allUserList.firstOrNull { it.userId == userId }?.apply {
                if (!groupIds.contains(groupId)) {
                    val newList = ArrayList<String>().apply {
                        addAll(groupIds)
                        add(groupId)
                    }
                    groupIds = newList
                }
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
                    MemoryMapShareApplication.allUserList.apply {
                        removeAll { it.userId == userId }
                        add(newUser)
                    }
                    sendFcm(
                        newUser,
                        TYPE_EDIT_GROUP,
                        MemoryMapShareApplication.applicationContext.getString(R.string.channel_name_3),
                        MemoryMapShareApplication.applicationContext.getString(R.string.channel_content_3, MemoryMapShareApplication.myUser.name)
                    )
                    groupEdited.postValue(groupId)
                }
        }
    }

    companion object {
        private const val TYPE_EDIT_GROUP = 2
    }
}