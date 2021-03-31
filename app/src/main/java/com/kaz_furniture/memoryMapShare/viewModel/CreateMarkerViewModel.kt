package com.kaz_furniture.memoryMapShare.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.data.MyMarker
import com.kaz_furniture.memoryMapShare.extensions.makeByteArray
import kotlinx.coroutines.launch
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
    var selectedGroupId: String? = null

    val locationNameInput = MutableLiveData<String>()
    val memoInput = MutableLiveData<String>()

    fun imageUpload(uriList: List<Uri>) {
        currentTimeMillis = System.currentTimeMillis().toString()
        imageListSize = uriList.size
        viewModelScope.launch {
            for ((index, value) in uriList.withIndex()) {
                FirebaseStorage.getInstance().reference.child("${myUser.userId}/${currentTimeMillis}/${index}.jpg")
                    .putBytes(value.makeByteArray())
                    .addOnCompleteListener {
                        Timber.d("uploaded = $index")
                        imageUploadedInt(index)
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

    }

    private fun submitMarker() {
        val marker = MyMarker().apply {
            userId = myUser.userId
            latLng = MyMarker.MyLatLng(latitude ?:return, longitude ?:return)
            memoryTime = calendar.time
            groupId = selectedGroupId ?: myUser.userId
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
        Timber.d("uploadedIndex = $index, $imageUploadedIntList, $imageListSize")
        if (imageUploadedIntList.size == imageListSize) imageUploadFinished.postValue(true)
    }

}