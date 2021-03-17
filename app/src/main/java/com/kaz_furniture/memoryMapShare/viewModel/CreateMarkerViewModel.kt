package com.kaz_furniture.memoryMapShare.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.data.Marker
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.util.*

class CreateMarkerViewModel: ViewModel() {
    var calendar: Calendar = Calendar.getInstance()
    var latitude: Double? = null
    var longitude: Double? = null

    fun imageUpload(uriList: List<Uri>) {
        for (value in uriList) {
            val inputStream = applicationContext.contentResolver.openInputStream(value)
            val bitmap = BitmapFactory.decodeStream(BufferedInputStream(inputStream))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ByteArrayOutputStream())

            FirebaseStorage.getInstance().reference.child("${calendar.time}/${calendar.time}.jpg")
        }
    }

    fun submitMarker() {
        val marker = Marker().apply {
            latLng = LatLng(latitude ?:return, longitude ?:return)
            memoryTime = calendar.time
        }
    }

}