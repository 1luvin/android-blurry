package com.luvin.blurry.util

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils

object ColorUtil {

    fun alpha(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
        return ColorUtils.setAlphaComponent(color, (255 * alpha).toInt())
    }
}