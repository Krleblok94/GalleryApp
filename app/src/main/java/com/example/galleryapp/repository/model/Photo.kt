package com.example.galleryapp.repository.model

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity (tableName = "photos")
data class Photo(
    @PrimaryKey
    @SerializedName("_id")
    @Expose
    val id: String,
    @SerializedName("comment")
    @Expose
    val comment: String? = "",
    @SerializedName("picture")
    @Expose
    var picture: String,
    @SerializedName("publishedAt")
    @Expose
    val publishedAt: String,
    var timestamp: Long = 0,
    var takenByUser: Boolean,
    @SerializedName("title")
    @Expose
    val title: String? = "") {

    fun print() {
        val tag = "DebugTag"
        Log.d(tag, "Id: $id, url: $picture, title: $title, published at: $publishedAt," +
                " timestamp: $timestamp, comment: $comment")
    }
}