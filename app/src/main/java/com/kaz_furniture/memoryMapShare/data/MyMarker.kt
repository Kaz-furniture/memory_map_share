package com.kaz_furniture.memoryMapShare.data

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

@IgnoreExtraProperties
class MyMarker: Serializable {
    var userId = ""
    var markerId = "${System.currentTimeMillis()}"
    var memoryTime = Date()
    var latLng = MyLatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    var createdAt = Date()
    var deletedAt: Date? = null
    var imageIdList = ArrayList<String>()
    var locationName = ""
    var memo = ""
    var groupId = ""

    var drawable1: Drawable? = null
    var drawable2: Drawable? = null

    class MyLatLng(var latitude: Double = DEFAULT_LATITUDE, var longitude: Double = DEFAULT_LONGITUDE)

    companion object {
        private const val DEFAULT_LATITUDE = 35.6598
        private const val DEFAULT_LONGITUDE = 139.7024
    }

}