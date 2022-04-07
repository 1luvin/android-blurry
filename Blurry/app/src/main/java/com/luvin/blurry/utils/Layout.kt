package com.luvin.blurry.utils

import android.widget.FrameLayout

class Layout
{
    companion object
    {
        const val MATCH_PARENT = -1
        const val WRAP_CONTENT = -2

        fun frame(width: Int, height: Int, gravity: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams(width, height, gravity).apply {
                setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
            }
        }

        fun frame(width: Int, height: Int, gravity: Int) : FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams(width, height, gravity)
        }

        fun frame(width: Int, height: Int) : FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams(width, height)
        }
    }
}































//