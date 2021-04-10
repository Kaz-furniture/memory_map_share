package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allGroupList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allMarkerList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allUserList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.data.MyMarker
import com.kaz_furniture.memoryMapShare.data.ShareGroup
import com.kaz_furniture.memoryMapShare.data.User
import java.util.*

class MapsViewModel: ViewModel() {
    val markerFinished = MutableLiveData<Boolean>()

    private fun fcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val fetchedToken = it.result ?:return@addOnCompleteListener
                if (fetchedToken != myUser.fcmToken) {
                    myUser.fcmToken = fetchedToken
                    uploadMyUser()
                } else return@addOnCompleteListener
            }
        }
    }

    private fun uploadMyUser() {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(myUser.userId)
            .set(myUser)
    }

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
                        fcmToken()
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
                .addOnCompleteListener { task ->
                    val result = task.result?.toObjects(MyMarker::class.java) ?: listOf()
                    allMarkerList.apply {
                        clear()
                        addAll(result.filter { it.deletedAt == null })
                        sortedBy { it.memoryTime }
                    }
                    markerFinished.postValue(true)
                }
    }
}