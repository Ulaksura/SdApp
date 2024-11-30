package com.example.sdapp.ui.gallery

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sdapp.R

class ImageAdapter(var images: List<Image>, var context: Context):RecyclerView.Adapter<ImageAdapter.MyViewHolder>(){
    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.imageGalleryPicture)
        val seed: TextView = view.findViewById(R.id.seedGalleryDisplay)
        val prompt:TextView = view.findViewById(R.id.promptGalleryDisplay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_gallery,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.seed.text = images[position].seed
        holder.prompt.text = images[position].prompt

        val bitmap = BitmapFactory.decodeByteArray(images[position].imageData, 0, images[position].imageData.size)
        holder.image.setImageBitmap(bitmap)
//        val imageId = context.resources.getIdentifier(
//            images[position].imageData.toString(),
//            "drawable",
//            context.packageName
//        )
//        holder.image.setImageResource(imageId)

    }
}