package com.luvin.blurry.util

import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.luvin.blurry.App
import com.luvin.blurry.R

class Theme
{
    companion object
    {
        val black get() = color(R.color.black)
        val white get() = color(R.color.white)
        val gray get() = color(R.color.gray)
        val red get() = color(R.color.red)
        val blue get() = color(R.color.blue)

        private fun color(colorKey: Int) : Int = ContextCompat.getColor(App.appContext(), colorKey)

        fun alphaColor(color: Int, alpha: Float) : Int
        {
            return ColorUtils.setAlphaComponent( color, (255 * alpha).toInt())
        }

        fun drawable(drawableKey: Int) : Drawable = ContextCompat.getDrawable(App.appContext(), drawableKey)!!

        fun rect(color: Int, radii: FloatArray? = null) : Drawable
        {
            val radiiArray = FloatArray(8)
            radii?.let {
                for (i in radii.indices)
                {
                    radiiArray[i*2] = it[i]
                    radiiArray[i*2 + 1] = it[i]
                }
            }

            return ShapeDrawable(
                RoundRectShape(radiiArray, null, null)
            ).apply {
                paint.color = color
            }
        }
    }
}































//