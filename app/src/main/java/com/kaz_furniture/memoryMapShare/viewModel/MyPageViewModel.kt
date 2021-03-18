package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.data.Marker
import timber.log.Timber

class MyPageViewModel: ViewModel() {
    val markersList = MutableLiveData<List<Marker>>()

    fun loadMarker() {
        FirebaseFirestore.getInstance()
                .collection("markers")
                .whereEqualTo("userId", myUser.userId)
                .get()
                .addOnCompleteListener {
                    val result = it.result?.toObjects(com.kaz_furniture.memoryMapShare.data.Marker::class.java) ?: listOf()
                    if (it.isSuccessful) {
                        markersList.postValue(result)
                    }
                }
    }
}