package com.kaz_furniture.memoryMapShare.data

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class User {
    var userId = ""
    var name = "ゲスト"
    var createdAt: Date = Date()
    var deletedAt: Date? = null
    var introduction: String = ""
    var imageUrl: String = ""
    var followingUserIds: List<String> = listOf()
    var fcmToken: String = ""
    var groupIds = listOf<String>()
}