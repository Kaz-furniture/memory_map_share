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
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allGroupList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allUserList
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.data.MyMarker
import com.kaz_furniture.memoryMapShare.extensions.makeByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class CreateMarkerViewModel: BaseViewModel() {
    var calendar: Calendar = Calendar.getInstance()
    var latitude: Double? = null
    var longitude: Double? = null
    private var currentTimeMillis = ""
    private val imageUrlList = ArrayList<String>()
    private val imageUploadedIntList = ArrayList<Int>()
    var imageListSize = 0
    val imageUploadFinished = MutableLiveData<Boolean>()
    var selectedGroupId: String? = null
    val uploadRatio = MutableLiveData<Float>()

    val locationNameInput = MutableLiveData<String>()
    val memoInput = MutableLiveData<String>()

    fun imageUpload(uriList: List<Uri>) {
        currentTimeMillis = System.currentTimeMillis().toString()
        imageListSize = uriList.size

        viewModelScope.launch {
            val pairs = uriList.withIndex().map { Pair(it.index, it.value.makeByteArray()) }
            withContext(Dispatchers.Main) {
                pairs.forEach { pair ->
                    FirebaseStorage.getInstance().reference.child("${myUser.userId}/${currentTimeMillis}/${pair.first}.jpg")
                        .putBytes(pair.second)
                        .addOnCompleteListener {
                            Timber.d("uploaded = ${pair.first}")
                            imageUploadedInt(pair.first)
                        }
                        .addOnFailureListener {
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
                .addOnCompleteListener {
                    sendFcmToEachUsers()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "SUBMIT_FAILED", Toast.LENGTH_SHORT).show()
                }
    }

    private fun sendFcmToEachUsers() {
        val groupId = selectedGroupId ?: return
        if (groupId.isBlank() || !allGroupList.map { it.groupId }.contains(groupId)) return
        allUserList.filter { it.groupIds.contains(groupId) }.forEach {
            sendFcm(it, TYPE_CREATE_MARKER, applicationContext.getString(R.string.channel_name_2), applicationContext.getString(R.string.channel_content_2, myUser.name))
        }
    }

    private fun imageUploadedInt(index: Int) {
        imageUploadedIntList.add(index)
        uploadRatio.postValue(imageUploadedIntList.size.toFloat() / imageListSize.toFloat())
        Timber.d("uploadedIndex = $index, $imageUploadedIntList, $imageListSize")
        if (imageUploadedIntList.size == imageListSize) imageUploadFinished.postValue(true)
    }

    companion object {
        private const val TYPE_CREATE_MARKER = 1
    }

}