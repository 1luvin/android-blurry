package com.luvin.blurry

import android.app.Application
import android.content.Context
import android.os.Environment
import coil.ImageLoader
import coil.request.CachePolicy
import com.luvin.blurry.util.Locale

class App : Application()
{
    companion object
    {
        @Volatile
        private var INSTANCE: App? = null
        fun instance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: App().also {
                    INSTANCE = it
                }
            }

        fun appContext() : Context
        {
            return instance().applicationContext
        }

        fun appDirPath() : String
        {
            return "${Environment.DIRECTORY_PICTURES}/${Locale.string(R.string.app_name)}"
        }

        lateinit var imageLoader: ImageLoader
            private set
    }

    override fun onCreate()
    {
        super.onCreate()
        INSTANCE = this

        createImageLoader()
    }

    private fun createImageLoader()
    {
        Companion.imageLoader = ImageLoader.Builder(appContext())
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCachePolicy( CachePolicy.DISABLED )
            .build()
    }
}


































//