package com.luvin.blurry.util

import android.widget.FrameLayout
import android.widget.LinearLayout

class Layout
{
    companion object
    {
        const val MATCH_PARENT = -1
        const val WRAP_CONTENT = -2

        // FrameLayout

        fun frame(
            width: Int, height: Int,
            gravity: Int,
            leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
        ) : FrameLayout.LayoutParams
        {
            return frame(width, height, gravity).apply {
                setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
            }
        }

        fun frame(width: Int, height: Int, gravity: Int) : FrameLayout.LayoutParams
        {
            return frame(width, height).apply {
                this.gravity = gravity
            }
        }

        fun frame(width: Int, height: Int) : FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams(width, height)
        }

        // LinearLayout

        fun linear(width: Int, height: Int, weight: Float) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams(width, height, weight)
        }
    }
}