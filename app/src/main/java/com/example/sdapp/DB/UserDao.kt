package com.example.sdapp.DB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImages(images: List<ImageEntity>)

    @Insert
    fun insertImage(image: ImageEntity)

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    fun getUserByLogin(login: String): User?

    @Query("SELECT * FROM users WHERE login = :login AND password = :password LIMIT 1")
    fun getUserByLoginAndPassword(login: String, password:String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Int): User?

    @Query("SELECT * FROM images WHERE user_id = :userId")
    fun getAllImages(userId: Int): List<ImageEntity>

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteImage(image: ImageEntity)
}
