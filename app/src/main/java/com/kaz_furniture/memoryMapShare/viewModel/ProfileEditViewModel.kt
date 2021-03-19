package com.kaz_furniture.memoryMapShare.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream

class ProfileEditViewModel: ViewModel() {
    val userNameInput = MutableLiveData<String>().apply {
        value = myUser.name
    }
    val imageUploadFinished = MutableLiveData<Boolean>()
    private val currentTime = System.currentTimeMillis()

    fun uploadIconImage(uri: Uri) {
        val inputStream = MemoryMapShareApplication.applicationContext.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(BufferedInputStream(inputStream))
        val bAOS = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 65, bAOS)
        val data = bAOS.toByteArray()

        FirebaseStorage.getInstance().reference.child("${myUser.userId}/iconImage/${currentTime}.jpg")
            .putBytes(data)
            .addOnCompleteListener {
                imageUploadFinished.postValue(true)
            }
    }

    fun uploadMyUser(noImage: Boolean) {
        val newUser = myUser.apply {
            name = userNameInput.value ?:return
            if (!noImage) imageUrl = "${myUser.userId}/iconImage/${currentTime}.jpg"
        }
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(myUser.userId)
            .set(newUser)
            .addOnCompleteListener {
                if (noImage) imageUploadFinished.postValue(true)
            }
    }
}