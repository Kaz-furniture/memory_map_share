package com.kaz_furniture.memoryMapShare.extensions

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.memoryMapShare.GlideApp
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.data.Marker
import timber.log.Timber

@BindingAdapter("exampleImageFirst")
fun ImageView.exampleImageFirst(marker: Marker) {
    if (marker.imageIdList.isNullOrEmpty()) setImageBitmap(null)
    else GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child(marker.imageIdList[0]))
            .placeholder(R.drawable.loading_image)
            .into(this)
}

@BindingAdapter("exampleImageSecond")
fun ImageView.exampleImageSecond(marker: Marker) {
    if (marker.imageIdList.size <= 1) setImageBitmap(null)
    else GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child(marker.imageIdList[1]))
            .placeholder(R.drawable.loading_image)
            .into(this)
}

@BindingAdapter("exampleImageThird")
fun ImageView.exampleImageThird(marker: Marker) {
    if (marker.imageIdList.size <= 2) setImageBitmap(null)
    else GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child(marker.imageIdList[2]))
            .placeholder(R.drawable.loading_image)
            .into(this)
}