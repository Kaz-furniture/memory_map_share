package com.kaz_furniture.memoryMapShare

import android.app.Application
import android.content.Context
import timber.log.Timber

class MemoryMapShareApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        MemoryMapShareApplication.applicationContext = applicationContext
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var applicationContext: Context
    }
}