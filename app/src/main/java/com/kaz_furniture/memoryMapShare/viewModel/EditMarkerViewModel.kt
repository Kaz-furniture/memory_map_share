package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allMarkerList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.R
import java.util.*

class EditMarkerViewModel: BaseViewModel() {
    var newDate: Date? = null
    val locationNameInput = MutableLiveData<String>()
    val memoInput = MutableLiveData<String>()
    val memoryTimeLiveData = MutableLiveData<String>()

    fun submitMarker(markerId: String) {
        val newMarker = allMarkerList.firstOrNull { it.markerId == markerId }?.apply {
            locationName = locationNameInput.value ?:""
            memo = memoInput.value ?:""
            newDate?.also {
                memoryTime = it
            }
            drawable1 = null
            drawable2 = null
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
                memoryTimeLiveData.postValue(android.text.format.DateFormat.format(applicationContext.getString(R.string.date), newMarker.memoryTime).toString())
            }
    }

    fun deleteMarker(markerId: String) {
        val newMarker = allMarkerList.firstOrNull { it.markerId == markerId }?.apply {
            deletedAt = Date()
            drawable1 = null
            drawable2 = null
        } ?:return

        FirebaseFirestore.getInstance()
            .collection("markers")
            .document(newMarker.markerId)
            .set(newMarker)
            .addOnCompleteListener {
                allMarkerList.apply {
                    removeAll {it.markerId == newMarker.markerId}
                }
            }
    }
}