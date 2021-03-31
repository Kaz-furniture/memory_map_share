package com.kaz_furniture.memoryMapShare.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream

fun Uri.makeByteArray(): ByteArray {
    val inputStream = MemoryMapShareApplication.applicationContext.contentResolver.openInputStream(this)
    val bitmap = BitmapFactory.decodeStream(BufferedInputStream(inputStream))
    val bAOS = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bAOS)
    return bAOS.toByteArray()
}