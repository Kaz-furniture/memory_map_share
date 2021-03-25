package com.kaz_furniture.memoryMapShare.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.memoryMapShare.GlideApp
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.allMarkerList
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.MyInfoWindowBinding
import com.kaz_furniture.memoryMapShare.extensions.exampleImageFirst
import timber.log.Timber
import java.lang.Exception

class MyInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private var isA = false
    private var isB = false


    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    override fun getInfoWindow(p0: Marker): View? {
        return setUpWindow(p0)
    }

    private fun setUpWindow(marker: Marker): View? {
        val myMarker = marker.tag as com.kaz_furniture.memoryMapShare.data.Marker?
                ?: return null
        val binding = MyInfoWindowBinding.inflate(LayoutInflater.from(context), null, false).apply {
//            markerForImage = myMarker
            locationName.text = myMarker.locationName
            textView.text = myMarker.memo
//            Timber.d("markerTag = ${markerForImage?.locationName}")
        }

        val url1 = myMarker.imageIdList.firstOrNull() ?:return null
        val url2 = if (myMarker.imageIdList.size <= 1) {
//            binding.imageViewSecond.setImageBitmap(null)
            ""
        } else {
            myMarker.imageIdList[1]
        }

//        if (myMarker.drawable1 == null) {
//            Picasso.get()
//                    .load(url1)
//                    .placeholder(R.drawable.loading)
//                    .into(binding.imageViewFirst, object: Callback {
//                        override fun onSuccess() {
//                            myMarker.drawable1 = binding.imageViewFirst.drawable
//                            check(myMarker, marker)
//                        }
//                        override fun onError(e: Exception?) {
//                            myMarker.drawable1 = null
//                            marker.showInfoWindow()
//                        }
//                    })
//        } else {
//            binding.imageViewFirst.setImageDrawable(myMarker.drawable1)
//        }
//
//        if (myMarker.drawable2 == null) {
//            Picasso.get()
//                    .load(url2)
//                    .placeholder(R.drawable.loading)
//                    .into(binding.imageViewSecond, object: Callback {
//                        override fun onSuccess() {
//                            myMarker.drawable2 = binding.imageViewSecond.drawable
//                            check(myMarker, marker)
//                        }
//                        override fun onError(e: Exception?) {
//                            myMarker.drawable2 = null
//                            marker.showInfoWindow()
//                        }
//                    })
//        } else {
//            binding.imageViewSecond.setImageDrawable(myMarker.drawable2)
//        }


        if (myMarker.drawable1 == null)
            GlideApp.with(context)
                    .load(FirebaseStorage.getInstance().reference.child(url1))
//                    .load(Uri.parse(url1))
                    .placeholder(R.drawable.loading)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            Timber.e("onLoadFailed message:${e?.message}")
                            myMarker.drawable1 = null
                            marker.showInfoWindow()
                            return true
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            myMarker.drawable1 = resource
                            Timber.d("checked 4, ${myMarker.drawable2}")
                            check(myMarker, marker)
                            return true
                        }
                    })
                    .into(binding.imageViewFirst)
        else
            binding.imageViewFirst.setImageDrawable(myMarker.drawable1)

        if (myMarker.drawable2 == null && url2.isNotEmpty())
            GlideApp.with(context)
                    .load(FirebaseStorage.getInstance().reference.child(url2))
//                    .load(Uri.parse(url2))
                    .placeholder(R.drawable.loading)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            Timber.e("onLoadFailed message:${e?.message}")
                            myMarker.drawable2 = null
                            marker.showInfoWindow()
                            return true
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            myMarker.drawable2 = resource
                            Timber.d("checked 3")
                            check(myMarker, marker)
                            return true
                        }
                    })
                    .into(binding.imageViewSecond)
        else if (url2.isEmpty()) {
            binding.imageViewSecond.visibility = View.GONE
            if (myMarker.drawable1 != null && myMarker.drawable1 == myMarker.drawable2)
                return binding.root
            else
                myMarker.drawable2 = myMarker.drawable1
                marker.showInfoWindow()
        }
        else
            binding.imageViewSecond.setImageDrawable(myMarker.drawable2)
        return binding.root
//        return LayoutInflater.from(context).inflate(R.layout.my_info_window, null, false).apply {
//            val markerForImage = marker.tag as com.kaz_furniture.memoryMapShare.data.Marker?
//            findViewById<TextView>(R.id.location_name).text = markerForImage?.locationName
//        }
    }

    private fun check(myMarker: com.kaz_furniture.memoryMapShare.data.Marker, marker: Marker) {
        Timber.d("checked 1")
        if (myMarker.drawable1 == null || myMarker.drawable2 == null) {
            Timber.d("checked 2")
            return
        }
        Handler(Looper.getMainLooper()).postDelayed({
            marker.showInfoWindow()
        }, 500L)
    }

//    companion object {
//        private val images = listOf(
//       "https://3.bp.blogspot.com/-rPT9gvA4Eq8/W5H_7F_BJ9I/AAAAAAABOww/GEEuplA1hewVcn6-pZPXHm_l6wiNM1LLACLcBGAs/s400/landmark_gozan_okuribi.png",
//       "https://2.bp.blogspot.com/-zei236VxgvY/W6S7Kyr0lgI/AAAAAAABPBU/PpD9Ac1Zigk8Mk5covLuSZtmSla6JbddQCLcBGAs/s400/landmark_moyai_shibuya.png",
//        "https://3.bp.blogspot.com/-6QX3erG9ovU/W4PKC69Ov0I/AAAAAAABOMQ/hPGvEo7eISUL6gpWzC-JcIQuWwkTs4rUwCLcBGAs/s450/shiro_maruokajou.png",
//                "https://3.bp.blogspot.com/-dcnB9OSUrdQ/VIhO9wM2BfI/AAAAAAAApkM/QjcnlylK2t4/s400/food_misonikomi_udon.png",
//                "https://1.bp.blogspot.com/-_EGcw4_K_Gg/X5OcdVnBt_I/AAAAAAABb-4/-x75puslh3YXxR9quzevd_YNViomCdACgCNcBGAsYHQ/s400/pet_robot_cat.png",
//                "https://3.bp.blogspot.com/--qqJ5m7XdB4/UpGGaSU-UnI/AAAAAAAAa4w/A6geCiDHlRQ/s400/nenkin_techou_blue.png",
//                "https://1.bp.blogspot.com/-1GNZtX4D-1k/UYOsrT-tZRI/AAAAAAAARKw/B95i7vBk7NQ/s400/umibiraki.png",
//                "https://2.bp.blogspot.com/-o6sqmMpBoog/WOsv-fapEbI/AAAAAAABDuY/dXKpBczL-HQIOeoIUieCT5KpRTOKuQPVQCLcB/s200/mark_checkbox2_green.png",
//                "https://2.bp.blogspot.com/-VZBfpqFAHAo/VlAY6KxjRTI/AAAAAAAA03A/KbMK1-25PCU/s400/food_nimono_satsumaimo.png",
//                "https://1.bp.blogspot.com/-f4F07DmvIq4/W-0g-cO2DeI/AAAAAAABQOI/faL_ty_L7x0QLHd1Hk4rUil5KIiPMU9IgCLcBGAs/s400/megane_sagasu_odeko_woman.png"
//                )
//    }
}