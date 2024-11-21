package com.example.sdapp.ui

import android.graphics.Bitmap
import kotlinx.coroutines.Job
import okhttp3.Request

//  Interface for communicating with MainActivity.kt
interface MainInterface {
    suspend fun generateImage(request: Request, prompt: String)
    fun displayError(error: String)
    fun onCancelGeneration()
    fun showImg2Img()
    fun uploadImage()
    fun getImage(): String?
    fun setImage(newImage: Bitmap, imageName: String)
    fun setGenerationCoroutine(generationCoroutine: Job)
}