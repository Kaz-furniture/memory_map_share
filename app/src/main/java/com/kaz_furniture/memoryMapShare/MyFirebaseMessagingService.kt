package com.kaz_furniture.memoryMapShare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kaz_furniture.memoryMapShare.activity.MapsActivity
import com.kaz_furniture.memoryMapShare.activity.MyPageActivity
import timber.log.Timber

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(p0)
        val data = remoteMessage.data
        Timber.d("remoteData = $data")
        when (data["type"]) {
            TYPE_CREATE_GROUP -> {
                val intent1 = Intent(this, MapsActivity::class.java)
                val resultPendingIntent1: PendingIntent? = TaskStackBuilder.create(this).run {
                    addNextIntent(intent1)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                val notification = NotificationCompat.Builder(this, CHANNEL_ID_1)
                    .setSmallIcon(R.drawable.ic_baseline_map_24)
                    .setContentIntent(resultPendingIntent1)
                    .setAutoCancel(true)
                    .setContentTitle(data["key1"])
                    .setContentText(data["key2"])
                    .build()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create the NotificationChannel
                    val name = getString(R.string.channel_name_1)
                    val descriptionText = getString(R.string.channel_description_1)
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
            TYPE_CREATE_MARKER -> {
                val intent2 = Intent(this, MyPageActivity::class.java)
                val resultPendingIntent2: PendingIntent? = TaskStackBuilder.create(this).run {
                    addParentStack(MyPageActivity::class.java)
                    addNextIntent(intent2)
                    getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                val notification = NotificationCompat.Builder(this, CHANNEL_ID_2)
                    .setSmallIcon(R.drawable.ic_baseline_map_24)
                    .setContentIntent(resultPendingIntent2)
                    .setAutoCancel(true)
                    .setContentTitle(data["key1"])
                    .setContentText(data["key2"])
                    .build()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create the NotificationChannel
                    val name = getString(R.string.channel_name_2)
                    val descriptionText = getString(R.string.channel_description_2)
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val mChannel = NotificationChannel(CHANNEL_ID_2, name, importance)
                    mChannel.description = descriptionText
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(mChannel)
                }
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(1, notification)
            }
            TYPE_EDIT_GROUP -> {
                val intent3 = Intent(this, MyPageActivity::class.java)
                val resultPendingIntent3: PendingIntent? = TaskStackBuilder.create(this).run {
                    addParentStack(MyPageActivity::class.java)
                    addNextIntent(intent3)
                    getPendingIntent(2, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                val notification = NotificationCompat.Builder(this, CHANNEL_ID_3)
                    .setSmallIcon(R.drawable.ic_baseline_map_24)
                    .setContentIntent(resultPendingIntent3)
                    .setAutoCancel(true)
                    .setContentTitle(data["key1"])
                    .setContentText(data["key2"])
                    .build()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create the NotificationChannel
                    val name = getString(R.string.channel_name_3)
                    val descriptionText = getString(R.string.channel_description_3)
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val mChannel = NotificationChannel(CHANNEL_ID_3, name, importance)
                    mChannel.description = descriptionText
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(mChannel)
                }
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(2, notification)
            }
            else -> return
        }



    }

    override fun onNewToken(p0: String) {
    }


    companion object {
        private const val TYPE_CREATE_GROUP = "0"
        private const val TYPE_CREATE_MARKER = "1"
        private const val TYPE_EDIT_GROUP = "2"
        private const val CHANNEL_ID_3 = "channel_id_3"
        private const val CHANNEL_ID_1 = "channel_id_1"
        private const val CHANNEL_ID_2 = "channel_id_2"
    }
}