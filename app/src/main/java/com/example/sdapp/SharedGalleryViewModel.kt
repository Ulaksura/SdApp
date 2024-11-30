package com.example.sdapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sdapp.ui.gallery.Image

class SharedGalleryViewModel : ViewModel() {

    private val _galleryImageList = MutableLiveData<List<Image>>(emptyList())
    val galleryImageList: LiveData<List<Image>> = _galleryImageList

    fun addImage(image: Image) {
        val updatedList = _galleryImageList.value.orEmpty().toMutableList()
        updatedList.add(image)
        _galleryImageList.value = updatedList
    }
}
