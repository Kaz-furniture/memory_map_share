package com.kaz_furniture.memoryMapShare.data

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@IgnoreExtraProperties
class Marker: Serializable {
    var markerId = "${System.currentTimeMillis()}"
    var memoryTime = Date()
    var latLng: LatLng? = null
    var deletedAt: Date? = null
    var imageIdList = ArrayList<String>()
    var memo = ""
    var groupId = ""

}