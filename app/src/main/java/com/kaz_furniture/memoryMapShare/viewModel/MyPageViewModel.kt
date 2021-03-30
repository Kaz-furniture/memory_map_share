package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allMarkerList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.data.Marker
import timber.log.Timber

class MyPageViewModel: ViewModel() {
    val markersList = MutableLiveData<Boolean>()
    val imageViewClicked = MutableLiveData<Marker>()

    fun launchAlbumActivity(myMarker: Marker) {
        imageViewClicked.postValue(myMarker)
    }

    fun loadMarker() {
        FirebaseFirestore.getInstance()
                .collection("markers")
                .get()
                .addOnCompleteListener { task ->
                    val result = task.result?.toObjects(com.kaz_furniture.memoryMapShare.data.Marker::class.java) ?: listOf()
                    if (task.isSuccessful) {
                        val fetchedList = ArrayList<Marker>().apply {
                            clear()
                            addAll(result)
                            sortedBy { it.memoryTime }
                        }
                        allMarkerList = fetchedList
                        markersList.postValue(true)
                    }
                }
    }
}