package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allGroupList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allMarkerList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allUserList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.data.Marker
import com.kaz_furniture.memoryMapShare.data.ShareGroup
import com.kaz_furniture.memoryMapShare.data.User
import java.util.*

class MapsViewModel: ViewModel() {
    val markerFinished = MutableLiveData<Boolean>()

    fun getAllUser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?:return

        FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val fetchedList = task.result?.toObjects(User::class.java) ?: listOf()
                        allUserList.apply {
                            clear()
                            addAll(fetchedList)
                        }
                        myUser = allUserList.firstOrNull { it.userId == userId } ?:return@addOnCompleteListener
                    }
                }
    }

    fun getAllGroup() {
        FirebaseFirestore.getInstance()
                .collection("groups")
                .get()
                .addOnCompleteListener {
                    val result = it.result?.toObjects(ShareGroup::class.java) ?: listOf()
                    allGroupList.apply {
                        clear()
                        addAll(result)
                    }
                }
    }

    fun getAllMarker() {
        FirebaseFirestore.getInstance()
                .collection("markers")
                .get()
                .addOnCompleteListener {
                    val result = it.result?.toObjects(Marker::class.java) ?: listOf()
                    allMarkerList.apply {
                        clear()
                        addAll(result)
                    }
                    markerFinished.postValue(true)
                }
    }
}