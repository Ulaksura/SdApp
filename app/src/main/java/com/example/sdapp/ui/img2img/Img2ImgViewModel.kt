package com.example.sdapp.ui.img2img

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Img2ImgViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Img2Img Fragment"
    }
    val text: LiveData<String> = _text
}