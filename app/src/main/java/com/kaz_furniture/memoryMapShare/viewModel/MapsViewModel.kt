package com.kaz_furniture.memoryMapShare.viewModel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.data.Marker
import com.kaz_furniture.memoryMapShare.data.User
import java.util.*
import kotlin.collections.ArrayList

class MapsViewModel: ViewModel() {
    fun getMyUser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?:return

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        myUser = it.result?.toObject(User::class.java) ?:return@addOnCompleteListener
                    }
                }
    }
}