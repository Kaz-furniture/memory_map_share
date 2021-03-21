package com.kaz_furniture.memoryMapShare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.MyInfoWindowBinding
import timber.log.Timber

class MyInfoWindowAdapter(private val context: Context): GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    override fun getInfoWindow(p0: Marker): View? {
        return setUpWindow(p0)
    }

    private fun setUpWindow(marker: Marker): View {
//        return MyInfoWindowBinding.inflate(LayoutInflater.from(context), null, false).apply {
//            val myMarker = marker.tag as com.kaz_furniture.memoryMapShare.data.Marker? ?:return@apply
//            markerForImage = myMarker
//            locationName.text = myMarker.locationName
//            Timber.d("markerTag = ${markerForImage?.locationName}")
//        }.root
        return LayoutInflater.from(context).inflate(R.layout.my_info_window, null, false).apply {
            val markerForImage = marker.tag as com.kaz_furniture.memoryMapShare.data.Marker?
            findViewById<TextView>(R.id.location_name).text = markerForImage?.locationName
        }
    }
}