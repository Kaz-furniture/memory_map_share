package com.kaz_furniture.memoryMapShare.data

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class ShareGroup {
    var groupId = "${System.currentTimeMillis()}"
    var createdAt = Date()
    var groupName = ""
}