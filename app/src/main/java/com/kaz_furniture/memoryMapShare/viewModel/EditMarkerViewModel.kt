package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allMarkerList
import java.util.*

class EditMarkerViewModel: BaseViewModel() {
    var newDate: Date? = null
    val locationNameInput = MutableLiveData<String>()
    val memoInput = MutableLiveData<String>()

    fun submitMarker(markerId: String) {
        val newMarker = allMarkerList.firstOrNull { it.markerId == markerId }?.apply {
            locationName = locationNameInput.value ?:""
            memo = memoInput.value ?:""
            newDate?.also {
                memoryTime = it
            }
        } ?:return

        FirebaseFirestore.getInstance()
            .collection("markers")
            .document(newMarker.markerId)
            .set(newMarker)
            .addOnCompleteListener {
                allMarkerList.apply {
                    removeAll { it.markerId == newMarker.markerId }
                    add(newMarker)
                }
            }
    }
}