package com.example.sdapp.dbo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageData: ByteArray,
    val url:String,
    val seed: String,
    val prompt: String
)