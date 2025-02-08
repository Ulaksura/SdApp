package com.example.sdapp

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sdapp.DB.ImageEntity
import java.io.File
import java.io.FileOutputStream

public var infoImage: ImageEntity = ImageEntity(0,0, byteArrayOf(0),
    "", "", "")
class ImageInfo: Fragment() {
    private lateinit var fileName: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageInfo = view.findViewById<ImageView>(R.id.imageGalleryPicture)
        val seedInfo = view.findViewById<TextView>(R.id.seedGalleryDisplay)
        val prompt = view.findViewById<TextView>(R.id.promptGalleryDisplay)
        val delButton = view.findViewById<Button>(R.id.delete)
        val saveButton = view.findViewById<Button>(R.id.saveImage)

        val imageData = infoImage.imageData
        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        imageInfo.setImageBitmap(bitmap)

        seedInfo.setText("Seed: ${infoImage.seed}")
        prompt.setText("Prompt: ${infoImage.prompt}")

        saveButton.setOnClickListener{
            saveImage(infoImage)
            saveButton.text = "Image saved"
        }

        delButton.setOnClickListener {
            deleteImage(requireContext(), infoImage)
        }

    }
    private fun saveImage(imageEntity: ImageEntity) {
        val imagesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        val imageCount = imageEntity.id
        val imageData = imageEntity.imageData
        fileName = "stabledif_$imageCount.jpg"

        val imageFile = File(imagesDirectory, fileName)
        FileOutputStream(imageFile).use { outputStream ->
            outputStream.write(imageData)
            outputStream.flush()
            MediaScannerConnection.scanFile(context, arrayOf(imageFile.absolutePath), null, null)
        }

    }

    private fun deleteImage(context: Context, imageEntity: ImageEntity){
        val userId = imageEntity.userId
        SharedGalleryViewModel().deleteImageFromDatabase(context,imageEntity, userId)
       // images.remove(imageEntity)
       // notifyItemRemoved(position)

        findNavController().navigate(R.id.navigation_gallery)
    }
}