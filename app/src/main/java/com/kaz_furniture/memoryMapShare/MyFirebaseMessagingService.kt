package com.kaz_furniture.memoryMapShare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(p0)
        val data = remoteMessage.data
        Timber.d("remoteData = $data")
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_baseline_more_horiz_24)
            .setContentTitle(data["key1"])
            .setContentText(data["key2"])
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID_1, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }

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
        private const val CHANNEL_ID_1 = "channel_id_1"
    }
}