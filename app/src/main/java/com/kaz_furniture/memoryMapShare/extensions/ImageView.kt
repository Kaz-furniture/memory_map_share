package com.kaz_furniture.memoryMapShare.extensions

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.memoryMapShare.GlideApp
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.data.MyMarker
import com.kaz_furniture.memoryMapShare.data.User

@BindingAdapter("exampleImageFirst")
fun ImageView.exampleImageFirst(myMarker: MyMarker) {
    if (myMarker.imageIdList.isNullOrEmpty()) setImageBitmap(null)
    else GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child(myMarker.imageIdList[0]))
            .placeholder(R.drawable.loading_image)
            .into(this)
}

@BindingAdapter("exampleImageSecond")
fun ImageView.exampleImageSecond(myMarker: MyMarker) {
    if (myMarker.imageIdList.size <= 1) setImageBitmap(null)
    else GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child(myMarker.imageIdList[1]))
            .placeholder(R.drawable.loading_image)
            .into(this)
}

@BindingAdapter("exampleImageThird")
fun ImageView.exampleImageThird(myMarker: MyMarker) {
    if (myMarker.imageIdList.size <= 2) setImageBitmap(null)
    else GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child(myMarker.imageIdList[2]))
            .placeholder(R.drawable.loading_image)
            .into(this)
}

@BindingAdapter("userIconFromUser")
fun ImageView.userIconFromUser(user: User?) {
    if (user == null || user.imageUrl.isBlank()) GlideApp.with(this).load(R.drawable.dog).circleCrop().placeholder(R.drawable.loading_image).into(this)
    else GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child(user.imageUrl))
            .circleCrop()
            .placeholder(R.drawable.loading_image)
            .into(this)
}

@BindingAdapter("albumViewFromId")
fun ImageView.albumViewFromId(imageId: String?) {
    imageId?.also {
        GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child(it))
            .placeholder(R.drawable.loading_image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    } ?: kotlin.run {
        GlideApp.with(this).load(R.drawable.loading_image).circleCrop().into(this)
    }
}