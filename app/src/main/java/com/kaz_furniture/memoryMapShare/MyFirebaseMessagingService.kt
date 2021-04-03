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
import timber.log.Timber

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(p0)
        val data = remoteMessage.data
        Timber.d("remoteData = $data")
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

    override fun onNewToken(p0: String) {
    }


    companion object {
        private const val CHANNEL_ID_1 = "channel_id_1"
    }
}