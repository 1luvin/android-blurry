package com.luvin.blurry.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import com.luvin.blurry.MainApplication
import kotlin.math.floor

class Utils
{
    companion object
    {
        private val density: Float = MainApplication.appContext().resources.displayMetrics.density

        fun dp(value: Int) : Int
        {
            if (value == 0) return 0
            return floor(density * value).toInt()
        }

        fun dp(value: Float) : Float
        {
            if (value == 0F) return 0F
            return floor(density * value)
        }

    }
}