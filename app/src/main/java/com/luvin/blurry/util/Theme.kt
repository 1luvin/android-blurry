package com.luvin.blurry.util

import androidx.core.content.ContextCompat
import com.luvin.blurry.App
import com.luvin.blurry.R

object Theme {
    val black get() = color(R.color.black)
    val white get() = color(R.color.white)
    val gray get() = color(R.color.gray)
    val red get() = color(R.color.red)
    val blue get() = color(R.color.blue)

    private fun color(colorKey: Int): Int = ContextCompat.getColor(App.appContext, colorKey)
}