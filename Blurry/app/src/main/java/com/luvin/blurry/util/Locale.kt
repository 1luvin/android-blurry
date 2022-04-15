package com.luvin.blurry.util

import com.luvin.blurry.App
import java.lang.Exception

class Locale
{
    companion object
    {
        fun string(stringKey: Int) : String
        {
            return try {
                App.appContext().resources.getString(stringKey)
            } catch (e: Exception) {
                "bad_string_key"
            }
        }
    }
}