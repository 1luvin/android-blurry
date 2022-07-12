package com.luvin.blurry.util

import com.luvin.blurry.App
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor

class AndroidUtil
{
    companion object
    {
        private val density: Float by lazy {
            App.appContext.resources.displayMetrics.density
        }

        fun dp(value: Int) : Int
        {
            if (value == 0) return 0
            return ceil(density * value).toInt()
        }

        fun dp(value: Float) : Float
        {
            if (value == 0f) return 0f
            return ceil(density * value)
        }
    }
}