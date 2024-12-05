package com.example.sdapp.dbo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert
    fun insertImage(image: ImageEntity)

    @Query("SELECT * FROM images")
    fun getAllImages(): List<ImageEntity>


    //@Delete
   // suspend fun deleteImage(image: ImageEntity)
}