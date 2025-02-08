package com.example.sdapp.DB
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id:Int =0,
    @ColumnInfo(name = "login") val login:String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "salt") val salt:String,
    @ColumnInfo(name = "email") val email:String,
    @ColumnInfo(name = "api_key") var apiKey: String?){
}

@Entity(
    tableName = "images",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "imageData") val imageData: ByteArray,
    @ColumnInfo(name = "url") val url:String,
    @ColumnInfo(name = "seed") val seed: String,
    @ColumnInfo(name = "prompt") val prompt: String
)
