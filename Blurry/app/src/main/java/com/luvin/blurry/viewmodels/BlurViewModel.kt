package com.luvin.blurry.viewmodels

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.luvin.blurry.MainApplication
import java.text.SimpleDateFormat
import java.util.*

class BlurViewModel : ViewModel()
{
    fun generatePhotoFileName() : String
    {
        val dateString = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "IMG_${dateString}.jpg"
    }

    fun generatePhotoContentValues() : ContentValues
    {
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, generatePhotoFileName())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, MainApplication.appDirPath())
        }
    }
}


































//