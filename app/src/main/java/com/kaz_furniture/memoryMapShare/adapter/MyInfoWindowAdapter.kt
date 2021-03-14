package com.kaz_furniture.memoryMapShare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.kaz_furniture.memoryMapShare.R

class MyInfoWindowAdapter(private val context: Context): GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    override fun getInfoWindow(p0: Marker): View? {
        return setUpWindow(p0)
    }

    private fun setUpWindow(marker: Marker): View {
        return LayoutInflater.from(context).inflate(R.layout.my_info_window, null, false).apply {
            findViewById<TextView>(R.id.textView).text = marker.title
        }
    }
}