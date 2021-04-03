package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allUserList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.data.ShareGroup
import com.kaz_furniture.memoryMapShare.data.User
import com.kaz_furniture.memoryMapShare.view.GroupMemberSelectView
import timber.log.Timber

class CreateGroupViewModel: ViewModel() {
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
        userAndCheckedList.filter { it.checked }.map { it.userId }.forEach { userId ->
            val newUser = allUserList.firstOrNull { it.userId == userId }?.apply {
                val newList = ArrayList<String>().apply {
                    addAll(groupIds)
                    add(groupId)
                }
                groupIds = newList
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
                        groupCreated.postValue(groupId)
                    }
        }
    }

}