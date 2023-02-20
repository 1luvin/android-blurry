package com.luvin.blurry.util

import androidx.annotation.IntRange
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

object PhotoUtil {

    fun randomBlurredPhotoUrl(
        width: Int, height: Int,
        @IntRange(from = 1, to = 10) blur: Int
    ): String {
        return "https://picsum.photos/$width/$height/?blur=$blur"
    }

    fun newPhotoFileName(): String {
        val dateString = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "IMG_${dateString}.jpg"
    }
}