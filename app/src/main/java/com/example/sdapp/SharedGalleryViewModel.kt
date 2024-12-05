package com.example.sdapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sdapp.dbo.ImageDatabase
import com.example.sdapp.dbo.ImageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SharedGalleryViewModel : ViewModel() {

    private val _galleryImageList = MutableLiveData<List<ImageEntity>>(emptyList())
    val galleryImageList: LiveData<List<ImageEntity>> = _galleryImageList

    fun addImage(image: ImageEntity) {
        val updatedList = _galleryImageList.value.orEmpty().toMutableList()
        updatedList.add(image)
        _galleryImageList.value = updatedList
    }

    fun addImageToDatabase(context: Context, imageEntity: ImageEntity) {
        val imageDao = ImageDatabase.getDatabase(context).imageDao()
        val image = imageEntity
       // imageList.add(image)
        CoroutineScope(Dispatchers.IO).launch {
            imageDao.insertImage(image)
        }
    }

    fun loadAllImagesFromDatabase(context: Context) {
        val imageDao = ImageDatabase.getDatabase(context).imageDao()
        val updatedList = _galleryImageList.value.orEmpty().toMutableList()

        CoroutineScope(Dispatchers.IO).launch {
            val images = imageDao.getAllImages()
            updatedList.addAll(images)

        }
        _galleryImageList.value = updatedList
    }

//    fun deleteImageFromDatabase(context: Context, image: ImageEntity, imageList: MutableList<ImageEntity>) {
//        val imageDao = ImageDatabase.getDatabase(context).imageDao()
//        imageList.remove(image)
//        CoroutineScope(Dispatchers.IO).launch {
//            imageDao.deleteImage(image)
//        }
//    }
}
