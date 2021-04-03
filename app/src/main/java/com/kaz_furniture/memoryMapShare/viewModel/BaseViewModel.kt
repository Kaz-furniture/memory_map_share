package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.activity.MapsActivity
import com.kaz_furniture.memoryMapShare.data.User
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.IOException

open class BaseViewModel: ViewModel() {

    fun sendFcm(sendToUser: User, key1: String, key2: String) {
        Timber.d("sendToUser = ${sendToUser.name}")
        if (sendToUser.userId == myUser.userId) return
        Timber.d("sendToUser = ${sendToUser.name}")
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        val fcmRequestBody = MapsActivity.FcmRequest().apply {
            to = sendToUser.fcmToken
            data.apply {
                this.key1 = key1
                this.key2 = key2
            }
        }
        val json = Gson().toJson(fcmRequestBody)
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .addHeader("Authorization", "key=${FCM_SERVER_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(MediaType.parse("application/json"), json))
            .build()
        Timber.d("json:$json")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Timber.d("onFailure e:${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                Timber.d("onResponse")
                response.body()?.string()?.also {
                    Timber.d("response:$it")
                }

            }
        })
    }

    companion object {
        private const val FCM_SERVER_KEY = "AAAA8HfBsYA:APA91bE3uUUxv7iq40wJOKoDc2TfK8fYcXHAuQ451etN-BLRU-ixxoOwAbyZvO6tsUSK_DxMD6F5YVXvwhNSBbi2y4ARPe2PFeSq5N54vwNYos0nay2ywr87wDBqKW5C-xNpucKpKdgd"
    }
}