package com.kaz_furniture.memoryMapShare.data

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@IgnoreExtraProperties
class Marker: Serializable {
    var userId = ""
    var markerId = "${System.currentTimeMillis()}"
    var memoryTime = Date()
    var latLng = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    var createdAt = Date()
    var deletedAt: Date? = null
    var imageIdList = ArrayList<String>()
    var locationName = ""
    var memo = ""
    var groupId = ""

    companion object {
        private const val DEFAULT_LATITUDE = 35.6598
        private const val DEFAULT_LONGITUDE = 139.7024
    }

}