package com.luvin.blurry.util

import com.luvin.blurry.App

class Locale
{
    companion object
    {
        fun string(stringKey: Int) = App.appContext().resources.getString(stringKey)
    }
}