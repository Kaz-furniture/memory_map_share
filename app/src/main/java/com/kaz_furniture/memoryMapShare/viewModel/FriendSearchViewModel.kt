package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.data.User

class FriendSearchViewModel: ViewModel() {
    val inputForSearch = MutableLiveData<String>()
    val searchedUsersList = MutableLiveData<List<User>>()

    fun searchUser() {
        val inputTextValue = inputForSearch.value ?:return
        FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnCompleteListener { task ->
                    val result = task.result?.toObjects(User::class.java) ?: listOf()
                    searchedUsersList.postValue(result.filter { it.name == inputTextValue || it.userId == inputTextValue })
                }
    }
}