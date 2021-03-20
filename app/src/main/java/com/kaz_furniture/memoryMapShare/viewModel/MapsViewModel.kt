package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allUserList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.data.User
import java.util.*

class MapsViewModel: ViewModel() {
    fun getMyUser() {
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
}