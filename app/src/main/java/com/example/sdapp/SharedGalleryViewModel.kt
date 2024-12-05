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
        val imageDao = ImageDatabase.getDatabase(context).imageDao()
        CoroutineScope(Dispatchers.IO).launch {
            imageDao.insertImage(imageEntity)
        }
        addImage(imageEntity)
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
    fun reLoadAllImagesFromDatabase(context: Context){
        val imageDao = ImageDatabase.getDatabase(context).imageDao()
        CoroutineScope(Dispatchers.IO).launch {
            val images = imageDao.getAllImages()

            withContext(Dispatchers.Main) {
                _galleryImageList.value = images
            }
        }
    }

    fun deleteImageFromDatabase(context: Context, image: ImageEntity) {
        val imageDao = ImageDatabase.getDatabase(context).imageDao()
        CoroutineScope(Dispatchers.IO).launch {
            imageDao.deleteImage(image)
            val updatedImages = imageDao.getAllImages()
            withContext(Dispatchers.Main) {
                _galleryImageList.value = updatedImages.toMutableList()
            }
        }
        //delImage(image)
    }
}
