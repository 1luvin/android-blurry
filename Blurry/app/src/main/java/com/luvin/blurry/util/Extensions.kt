package com.luvin.blurry.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable

// Drawable

fun Drawable.asBitmap() : Bitmap
{
    val bitmap = Bitmap.createBitmap(
        intrinsicWidth,
        intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    draw(canvas)

    return bitmap
}