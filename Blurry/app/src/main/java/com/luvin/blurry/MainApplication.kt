package com.luvin.blurry

import android.app.Application
import android.content.Context
import android.os.Environment
import com.luvin.blurry.utils.Locale

class MainApplication : Application()
{
    companion object
    {
        @Volatile
        private var INSTANCE: MainApplication? = null
        fun instance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MainApplication().also {
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
    }

    override fun onCreate()
    {
        super.onCreate()

        INSTANCE = this
    }
}


































//