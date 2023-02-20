package com.luvin.blurry.extension

import com.luvin.blurry.App
import kotlin.math.roundToInt

private val density: Float get() = App.appContext.resources.displayMetrics.density

/*
    Int
 */

val Int.dp
    get(): Int {
        return if (this == 0) {
            0
        } else {
            (this * density).roundToInt()
        }
    }

/*
    Float
 */

val Float.dp
    get(): Float {
        return if (this == 0f) {
            0f
        } else {
            this * density
        }
    }

val Float.px get(): Float {
    return if (this == 0f) {
        0f
    } else {
        this / density
    }
}