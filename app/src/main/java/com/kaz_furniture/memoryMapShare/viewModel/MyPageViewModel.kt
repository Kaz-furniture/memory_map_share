package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allMarkerList
import com.kaz_furniture.memoryMapShare.data.MyMarker

class MyPageViewModel: ViewModel() {
    val markersList = MutableLiveData<Boolean>()
    val imageViewClicked = MutableLiveData<MyMarker>()

    fun launchAlbumActivity(myMyMarker: MyMarker) {
        imageViewClicked.postValue(myMyMarker)
    }

    fun loadMarker() {
        FirebaseFirestore.getInstance()
                .collection("markers")
                .get()
                .addOnCompleteListener { task ->
                    val result = task.result?.toObjects(com.kaz_furniture.memoryMapShare.data.MyMarker::class.java) ?: listOf()
                    if (task.isSuccessful) {
                        val fetchedList = ArrayList<MyMarker>().apply {
                            clear()
                            addAll(result.filter { it.deletedAt == null })
                            sortedBy { it.memoryTime }
                        }
                        allMarkerList = fetchedList
                        markersList.postValue(true)
                    }
                }
    }
}