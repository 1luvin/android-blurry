package com.luvin.blurry

import android.app.Application
import android.content.Context

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
    }

    override fun onCreate()
    {
        super.onCreate()

        INSTANCE = this
    }
}