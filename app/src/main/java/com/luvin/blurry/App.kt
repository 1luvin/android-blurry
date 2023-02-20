package com.luvin.blurry

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import coil.ImageLoader
import coil.request.CachePolicy

class App : Application() {

    companion object {
        private lateinit var instance: App
        val appContext: Context get() = instance.applicationContext

        val savePath: String get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${Environment.DIRECTORY_PICTURES}/Blurry"
        } else {
            "${Environment.getExternalStorageDirectory().absolutePath}/Pictures/Blurry/"
        }

        val imageLoader: ImageLoader by lazy {
            ImageLoader.Builder(appContext)
                .diskCachePolicy(CachePolicy.DISABLED)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}