package com.luvin.blurry.util

import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.luvin.blurry.App
import com.luvin.blurry.R
import java.lang.Exception

class Theme
{
    companion object
    {
        val white get() = color(R.color.white)
        val black get() = color(R.color.black)

        fun color(colorKey: Int) : Int
        {
            return try {
                ContextCompat.getColor(
                    App.appContext(),
                    colorKey
                )
            } catch (e: Exception) {
                0xFF000000.toInt()
            }
        }

        fun alphaColor(color: Int, alpha: Float) : Int
        {
            return ColorUtils.setAlphaComponent( color, (255 * alpha).toInt())
        }

        fun drawable(drawableKey: Int) : Drawable
        {
            return ContextCompat.getDrawable(
                App.appContext(),
                drawableKey
            )!!
        }

        fun rect(colorKey: Int, radii: FloatArray? = null) : Drawable
        {
            val radiiArray = FloatArray(8)
            if (radii != null) {
                for (i in radii.indices)
                {
                    radiiArray[i*2] = radii[i]
                    radiiArray[i*2 + 1] = radii[i]
                }
            }

            return ShapeDrawable(
                RoundRectShape(radiiArray, null, null)
            ).apply {
                paint.color = color(colorKey)
            }
        }

    }
}































//