package com.example.galleryapp.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.galleryapp.repository.model.Photo

@Database (entities = [Photo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao() : PhotoDao
}