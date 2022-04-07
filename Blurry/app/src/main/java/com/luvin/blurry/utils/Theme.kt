package com.luvin.blurry.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import androidx.annotation.IntegerRes
import androidx.core.content.ContextCompat
import com.luvin.blurry.MainApplication
import com.luvin.blurry.R
import java.lang.Exception

class Theme
{
    companion object
    {
        const val WHITE = 0xFFFFFFFF.toInt()

        fun color(colorKey: Int) : Int {
            return try {
                ContextCompat.getColor(
                    MainApplication.appContext(),
                    colorKey
                )
            } catch (e: Exception) {
                println("SFSSDFSDG")
                0xFF000000.toInt()
            }
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