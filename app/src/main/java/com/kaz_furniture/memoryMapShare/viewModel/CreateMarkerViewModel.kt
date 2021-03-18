package com.kaz_furniture.memoryMapShare.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.data.Marker
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class CreateMarkerViewModel: ViewModel() {
    var calendar: Calendar = Calendar.getInstance()
    var latitude: Double? = null
    var longitude: Double? = null
    val imageUploaded = MutableLiveData<Int>()
    private var currentTimeMillis = ""
    private val imageUrlList = ArrayList<String>()
    private val imageUploadedIntList = ArrayList<Int>()
    var imageListSize = 0
    val imageUploadFinished = MutableLiveData<Boolean>()

    val locationNameInput = MutableLiveData<String>()
    val memoInput = MutableLiveData<String>()

    fun imageUpload(uriList: List<Uri>) {
        currentTimeMillis = System.currentTimeMillis().toString()
        imageListSize = uriList.size
        for ((index, value) in uriList.withIndex()) {
            val inputStream = applicationContext.contentResolver.openInputStream(value)
            val bitmap = BitmapFactory.decodeStream(BufferedInputStream(inputStream))
            val bAOS = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bAOS)
            val data = bAOS.toByteArray()

            FirebaseStorage.getInstance().reference.child("${myUser.userId}/${currentTimeMillis}/${index}.jpg")
                    .putBytes(data)
                    .addOnCompleteListener {
                        Timber.d("uploaded = $index")
                        imageUploaded.postValue(index)
                        Toast.makeText(applicationContext, "upload $index", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        imageUploaded.postValue(index)
                        Toast.makeText(applicationContext, "UPLOAD_FAILED", Toast.LENGTH_SHORT).show()
                    }
        }
        imageUrlList.clear()
        for ((index) in uriList.withIndex()) {
            imageUrlList.add("${myUser.userId}/${currentTimeMillis}/${index}.jpg")
        }
        submitMarker()
    }

    private fun submitMarker() {
        val marker = Marker().apply {
            userId = myUser.userId
            latLng = Marker.MyLatLng(latitude ?:return, longitude ?:return)
            memoryTime = calendar.time
            groupId = myUser.userId
            imageIdList = imageUrlList
            memo = memoInput.value ?:""
            locationName = locationNameInput.value ?:""
        }

        FirebaseFirestore.getInstance()
                .collection("markers")
                .document(marker.markerId)
                .set(marker)
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "SUBMIT_FAILED", Toast.LENGTH_SHORT).show()
                }
    }

    fun imageUploadedInt(index: Int) {
        imageUploadedIntList.add(index)
        Timber.d("uploadedIndex = $index, $imageUploadedIntList")
        if (imageUploadedIntList.size == imageListSize) imageUploadFinished.postValue(true)
    }

}