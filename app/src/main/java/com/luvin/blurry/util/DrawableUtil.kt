package com.luvin.blurry.util

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt

object DrawableUtil {

    val CORNER_MAX: Float get() = Float.MAX_VALUE

    fun rect(@ColorInt color: Int): Drawable {
        return rect(color, null)
    }

    fun rect(@ColorInt color: Int, corner: Float): Drawable {
        return rect(color, corners = FloatArray(4) { corner })
    }

    fun rect(@ColorInt color: Int, corners: FloatArray?): Drawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            corners?.let {
                cornerRadii = createCorners(it)
            }
        }
    }

    private fun createCorners(corners: FloatArray): FloatArray {
        val a = FloatArray(8)
        for (i in corners.indices) {
            a[i * 2] = corners[i]
            a[i * 2 + 1] = corners[i]
        }

        return a
    }
}