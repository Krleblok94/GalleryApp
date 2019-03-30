package com.example.galleryapp.repository.network

import com.example.galleryapp.repository.model.Photo
import com.example.galleryapp.util.Constants
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET(Constants.END_POINT)
    fun loadPhotos() : Call<List<Photo>>
}