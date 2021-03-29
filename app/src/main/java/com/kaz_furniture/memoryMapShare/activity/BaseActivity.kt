package com.kaz_furniture.memoryMapShare.activity

import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication
import com.kaz_furniture.memoryMapShare.R

open class BaseActivity: AppCompatActivity() {

    fun locationPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    protected fun hideKeyboard(view: View) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    fun savedGroupText(savedGroupId: String?): String {
        return if (savedGroupId.isNullOrBlank()) getString(R.string.privateText)
        else MemoryMapShareApplication.allGroupList.firstOrNull {it.groupId == savedGroupId}?.groupName ?:getString(
            R.string.privateText)
    }

}