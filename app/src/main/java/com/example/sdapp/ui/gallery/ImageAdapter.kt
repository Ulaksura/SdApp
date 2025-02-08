package com.example.sdapp.ui.gallery

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.sdapp.DB.ImageEntity
import com.example.sdapp.R
import com.example.sdapp.SharedGalleryViewModel
import com.example.sdapp.ui.MainInterface
import java.io.File
import java.io.FileOutputStream


class ImageAdapter(var images: MutableList<ImageEntity>, var context: Context,
                   private val onImageClick: (ImageEntity) -> Unit
):RecyclerView.Adapter<ImageAdapter.MyViewHolder>(){

    private lateinit var mainInterface: MainInterface
    private lateinit var fileName: String



    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.imageGalleryPicture)

//        val seed: TextView = view.findViewById(R.id.seedGalleryDisplay)
//        val prompt:TextView = view.findViewById(R.id.promptGalleryDisplay)
//
//        val saveElement = view.findViewById<Button>(R.id.saveImage)
//        val deleteElement = view.findViewById<Button>(R.id.delete)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_gallery,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.seed.text = images[position].seed
//        holder.prompt.text = images[position].prompt

        val imageCount = context.resources.getIdentifier(
            images[position].imageData.toString(),
            "drawable",
            context.packageName
        )

        holder.image.setOnClickListener {
            onImageClick(images[position])
        }

//        holder.saveElement.setOnClickListener {
//            saveImage(imageCount,images[position].imageData)
//            holder.saveElement.text = "Image saved"
//        }
//        holder.deleteElement.setOnClickListener {
//            val img = images[position]
//            deleteImage(context, img, position, authUser.idAuthUser)
//            notifyItemRangeChanged(position,images.size)
//        }


        val bitmap = BitmapFactory.decodeByteArray(images[position].imageData, 0, images[position].imageData.size)
        holder.image.setImageBitmap(bitmap)

    }




    private fun saveImage(imageCount:Int, imageData:ByteArray) {
        val imagesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        fileName = "stabledif_$imageCount.jpg"

        val imageFile = File(imagesDirectory, fileName)
        FileOutputStream(imageFile).use { outputStream ->
            outputStream.write(imageData)
            outputStream.flush()
            MediaScannerConnection.scanFile(context, arrayOf(imageFile.absolutePath), null, null)
        }

    }

    private fun deleteImage(context: Context, imageEntity: ImageEntity, position: Int, userId: Int){
        SharedGalleryViewModel().deleteImageFromDatabase(context,imageEntity, userId)
        images.remove(imageEntity)
        notifyItemRemoved(position)
    }
}