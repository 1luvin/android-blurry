package com.luvin.blurry.utils

import androidx.core.content.ContextCompat
import com.luvin.blurry.MainApplication
import java.lang.Exception

class Locale
{
    companion object
    {
        fun string(stringKey: Int) : String
        {
            return try {
                MainApplication.appContext().resources.getString(stringKey)
            } catch (e: Exception) {
                "bad_string_key"
            }
        }
    }
}