package com.luvin.blurry.util

import com.luvin.blurry.App
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import kotlin.math.floor

class Utils
{
    companion object
    {
        private val density: Float = App.appContext().resources.displayMetrics.density

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

        fun randomPhotoUrl() : String = "https://picsum.photos/200/400/?blur=10"

        fun newPhotoFileName() : String
        {
            val dateString = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            return "IMG_${dateString}.jpg"
        }

    }
}


































//