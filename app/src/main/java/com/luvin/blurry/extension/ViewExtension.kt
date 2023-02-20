package com.luvin.blurry.extension

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children

/*
    View
 */

fun View.setEnabledWithChildren(enabled: Boolean) {
    isEnabled = enabled
    (this as? ViewGroup)?.children?.forEach {
        it.setEnabledWithChildren(enabled)
    }
}

/*
    TextView
 */

var TextView.textSizeDp: Float
    get() = textSize.px
    set(value) = setTextSize(TypedValue.COMPLEX_UNIT_DIP, value)