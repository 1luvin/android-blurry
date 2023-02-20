package com.luvin.blurry.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.luvin.blurry.App

object Resource {

    private val context: Context get() = App.appContext

    fun drawable(@DrawableRes drawableKey: Int): Drawable {
        return ContextCompat.getDrawable(context, drawableKey)!!
    }

    fun string(@StringRes stringKey: Int): String {
        return context.resources.getString(stringKey)
    }
    fun string(@StringRes stringKey: Int, vararg args: Any): String {
        return String.format(string(stringKey), *args)
    }
}