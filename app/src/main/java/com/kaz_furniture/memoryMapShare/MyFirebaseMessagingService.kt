package com.kaz_furniture.memoryMapShare

import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

//    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
//        remoteMessage?.data?.also { data ->
//            val title = data["title"]
//            val message = data["message"]
//            val builder = if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
//                NotificationCompat.Builder(this, CHANNEL_ID)
//            } else {
//                NotificationCompat.Builder(this)
//            }
//            val notification = builder
//                .setSmallIcon(R.drawable.dog)     // アイコンは指定必須です
//                .setContentTitle(title)                 // 通知に表示されるタイトルです
//                .setContentText(message)                // 通知内容を設定します
//                .build()
//            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            nm.notify(0, notification)
//        }
//    }

//    override fun onNewToken(p0: String) {
//        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        // Android O(8.0) 以上で通知を使用する場合は通知チャンネルを作成する必要があります
//        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
//            var channel = nm.getNotificationChannel(CHANNEL_ID)
//            if (channel == null) {
//                channel = NotificationChannel(
//                    CHANNEL_ID,
//                    "プッシュ通知用のチャンネルです",
//                    NotificationManager.IMPORTANCE_HIGH)
//                nm.createNotificationChannel(channel)
//            }
//        }
//    }

    companion object {
        private const val CHANNEL_ID = "channel_id"
    }
}