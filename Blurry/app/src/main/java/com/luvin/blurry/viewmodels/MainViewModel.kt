package com.luvin.blurry.viewmodels

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel()
{
    fun randomPhotoUrl() : String
    {
        return "https://picsum.photos/200/400/?blur=10"
    }
}