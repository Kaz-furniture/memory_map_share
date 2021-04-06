package com.kaz_furniture.memoryMapShare.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allGroupList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allUserList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.view.GroupMemberEditView
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class EditGroupViewModel: BaseViewModel() {
    var groupId = ""
    val groupNameInput = MutableLiveData<String>()
    val userAndCheckedListFirst = ArrayList<GroupMemberEditView.Adapter.UserAndChecked>()
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
                }
        }
        val formerCheckedList = arrayListOf<GroupMemberEditView.Adapter.UserAndChecked>().apply {
            addAll(userAndCheckedListFirst)
        }.filter { it.checked }
        val latterUncheckedList = arrayListOf<GroupMemberEditView.Adapter.UserAndChecked>().apply {
            addAll(userAndCheckedList)
        }.filter { !it.checked }
        Timber.d("checkedList = ${formerCheckedList.map { it.userId }}, ${latterUncheckedList.map { it.userId }}")
        val changedToFalseList = formerCheckedList.filter { value -> latterUncheckedList.map { it.userId }.contains(value.userId) }
        changedToFalseList.map { it.userId }.forEach { userId ->
            val newUser = allUserList.firstOrNull { it.userId == userId }?.apply {
                val newGroupIds = arrayListOf<String>().apply {
                    addAll(groupIds)
                    removeAll { it == groupId }
                }
                groupIds = newGroupIds
            } ?:return@forEach

            Timber.d("checkedList2 = ${newUser.name}")

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .set(newUser)
                .addOnCompleteListener {
                    allUserList.apply {
                        removeAll { it.userId == userId }
                        add(newUser)
                    }
                }
        }
        groupEdited.postValue(groupId)
    }

    fun deleteGroup() {
        val newGroup = allGroupList.firstOrNull { it.groupId == groupId }?.apply {
            deletedAt = Date()
        } ?:return
        FirebaseFirestore.getInstance()
            .collection("groups")
            .document(groupId)
            .set(newGroup)
            .addOnCompleteListener {
                allGroupList.apply {
                    removeAll { it.groupId == groupId }
                    add(newGroup)
                }
                Toast.makeText(applicationContext, "グループを削除しました", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TYPE_EDIT_GROUP = 2
    }
}