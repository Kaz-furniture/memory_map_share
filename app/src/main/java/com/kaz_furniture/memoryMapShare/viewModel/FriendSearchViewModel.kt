package com.kaz_furniture.memoryMapShare.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allUserList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.data.User

class FriendSearchViewModel: ViewModel() {
    val inputForSearch = MutableLiveData<String>()
    val searchedUsersList = MutableLiveData<List<User>>()
    val buttonClicked = MutableLiveData<Boolean>()

    fun searchUser() {
        val inputTextValue = inputForSearch.value ?:return
        FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnCompleteListener { task ->
                    val result = task.result?.toObjects(User::class.java) ?: listOf()
//                    searchedUsersList.postValue(result.filter { it.name == inputTextValue || it.userId == inputTextValue })
                    searchedUsersList.postValue(result.filter { inputTextValue.toRegex().containsMatchIn(it.name) || inputTextValue.toRegex().containsMatchIn(it.userId) })
                }
    }

    fun addButtonClick(user: User) {
        val newUsersList = ArrayList<String>().apply {
            addAll(myUser.followingUserIds)
            if (myUser.followingUserIds.contains(user.userId)) remove(user.userId) else add(user.userId)
        }
        myUser.followingUserIds = newUsersList
        allUserList.apply {
            removeAll { it.userId == myUser.userId }
            add(myUser)
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(myUser.userId)
                .set(myUser)
                .addOnCompleteListener {
                    buttonClicked.postValue(true)
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                }
    }
}