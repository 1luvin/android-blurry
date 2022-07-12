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
        private var Instance: App? = null
        fun instance() =
            Instance ?: synchronized(this) {
                Instance ?: App().also {
                    Instance = it
                }
            }

        val appContext: Context get() = instance().applicationContext

        fun appDirPath() : String
        {
            return "${Environment.DIRECTORY_PICTURES}/Blurry"
        }

        val imageLoader: ImageLoader by lazy {
            ImageLoader.Builder(appContext)
                .diskCachePolicy(CachePolicy.DISABLED)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .build()
        }
    }

    override fun onCreate()
    {
        super.onCreate()
        Instance = this
    }
}


































//