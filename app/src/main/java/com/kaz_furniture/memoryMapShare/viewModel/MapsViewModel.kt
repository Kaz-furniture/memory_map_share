package com.kaz_furniture.memoryMapShare.viewModel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.data.Marker
import java.util.*
import kotlin.collections.ArrayList

class MapsViewModel: ViewModel() {
    val markerList = ArrayList<Marker>()
    var selectedLocation: LatLng? = null

    fun createMaker() {
        val newMarker = Marker().apply {
            memoryTime = Date()
            latLng = selectedLocation
            //追加！！！！！
        }
        FirebaseFirestore.getInstance()
                .collection("marker")
                .document(newMarker.markerId)
                .set(newMarker)
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "失敗しました", Toast.LENGTH_SHORT).show()
                }
    }
}