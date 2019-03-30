package com.example.galleryapp.repository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.galleryapp.repository.model.Photo

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photos ORDER BY photos.timestamp ASC")
    fun getPhotos() : LiveData<List<Photo>>

    @Query("SELECT * FROM photos WHERE id = :id")
    fun getPhotoById(id: String) : LiveData<Photo>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotos(photos: List<Photo>)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertPhoto(photo: Photo)
}