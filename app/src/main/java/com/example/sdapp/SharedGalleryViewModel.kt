package com.example.sdapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sdapp.DB.AppDatabase
import com.example.sdapp.DB.ImageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SharedGalleryViewModel : ViewModel() {

    private val _galleryImageList = MutableLiveData<List<ImageEntity>>(emptyList())
    val galleryImageList: LiveData<List<ImageEntity>> = _galleryImageList

    fun addImage(image: ImageEntity) {
        val updatedList = _galleryImageList.value.orEmpty().toMutableList()
        updatedList.add(image)
        _galleryImageList.value = updatedList
    }
    fun delImage(image: ImageEntity){
        val updatedList = _galleryImageList.value.orEmpty().toMutableList()
        updatedList.remove(image)
        _galleryImageList.value = updatedList
    }
    fun addImageToDatabase(context: Context, imageEntity: ImageEntity) {
        val imageDao = AppDatabase.getInstance(context).userDao()
        CoroutineScope(Dispatchers.IO).launch {
            imageDao.insertImage(imageEntity)
        }
        addImage(imageEntity)
    }

    fun loadAllImagesFromDatabase(context: Context, userId: Int) {
        val imageDao = AppDatabase.getInstance(context).userDao()
        val updatedList = _galleryImageList.value.orEmpty().toMutableList()
        CoroutineScope(Dispatchers.IO).launch {
            val images = imageDao.getAllImages(userId)
            updatedList.addAll(images)
        }
        _galleryImageList.value = updatedList
    }
    fun reLoadAllImagesFromDatabase(context: Context, userId: Int){
        val imageDao = AppDatabase.getInstance(context).userDao()
        CoroutineScope(Dispatchers.IO).launch {
            val images = imageDao.getAllImages(userId)

            withContext(Dispatchers.Main) {
                _galleryImageList.value = images
            }
        }
    }

    fun exitUserGallery(context: Context, userId: Int){
        _galleryImageList.value = emptyList()
    }

    fun deleteImageFromDatabase(context: Context, image: ImageEntity, userId: Int) {
        val imageDao = AppDatabase.getInstance(context).userDao()
        CoroutineScope(Dispatchers.IO).launch {
            imageDao.deleteImage(image)
            val updatedImages = imageDao.getAllImages(userId)
            withContext(Dispatchers.Main) {
                _galleryImageList.value = updatedImages.toMutableList()
            }
        }
        //delImage(image)
    }
}
