package com.example.galleryapp.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.galleryapp.R
import com.example.galleryapp.repository.database.AppDatabase
import com.example.galleryapp.repository.model.Photo
import com.example.galleryapp.repository.network.ApiService
import com.example.galleryapp.util.*
import retrofit2.Call
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class PhotoRepository(private val appExecutors: AppExecutors,
                      private val appDatabase: AppDatabase,
                      private val apiService: ApiService) {

    private val rateLimiter = RateLimiter<String>(5, TimeUnit.MINUTES)
    private val missingPhotoIds = intArrayOf(86, 97, 105, 138, 148, 150, 205, 207, 224, 226,
        245, 246, 262, 285, 286, 298, 303, 332, 333, 346, 359, 394, 414, 422, 438, 462, 463, 470, 489)

    fun loadPhotos() : LiveData<Resource<List<Photo>>> = object :
        NetworkBoundResource<List<Photo>, List<Photo>>(appExecutors) {

        override fun saveCallResult(item: List<Photo>) {
            val random = Random(System.currentTimeMillis())
            for (photo in item) {
                var randomInt = random.nextInt(0, 500)
                // If it's one of the missing id's, get a random number until it's not in the collection
                while (missingPhotoIds.contains(randomInt)) {
                    randomInt = random.nextInt(0, 450)
                }
                photo.picture = "https://picsum.photos/600/300/?image=$randomInt"
                photo.timestamp = DateUtils.convertToTimestampFromString(photo.publishedAt)
                photo.takenByUser = false
            }
            appDatabase.photoDao().insertPhotos(item)
        }

        override fun shouldFetch(data: List<Photo>?): Boolean =
            rateLimiter.shouldFetch(Constants.API_RATE_LIMITER) || data == null || data.isEmpty()

        override fun loadFromDb(): LiveData<List<Photo>> = appDatabase.photoDao().getPhotos()

        override fun createCall(): Call<List<Photo>>? = apiService.loadPhotos()

        override fun onFetchFailed() {
            rateLimiter.reset(Constants.API_RATE_LIMITER)
        }

    }.asLiveData()

    fun loadPhoto(id: String) : LiveData<Resource<Photo>> = object :
        NetworkBoundResource<Photo, Photo>(appExecutors) {

        override fun saveCallResult(item: Photo) = Unit

        override fun shouldFetch(data: Photo?): Boolean = false

        override fun loadFromDb(): LiveData<Photo> = appDatabase.photoDao().getPhotoById(id)

        override fun createCall(): Call<Photo>? = null

    }.asLiveData()

    fun savePhoto(photo: Photo) {
        appExecutors.diskIO().execute {
            appDatabase.photoDao().insertPhoto(photo)
        }
    }

    fun sharePhoto(context: Context, photo: Photo?, glide: GlideRequests, shareStatus: MutableLiveData<Int>) {
        if (photo != null) {
            appExecutors.diskIO().execute {
                shareStatus.postValue(Constants.Status.LOADING)
                val imageUri: Uri = if (photo.takenByUser) {
                    FileUtils.getLocalBitmapUri(context, photo.picture)
                } else {
                    FileUtils.getUrlBitmapUri(context, glide.asBitmap().load(photo.picture).submit().get())
                }

                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    type = "image/jpeg"
                }

                shareStatus.postValue(Constants.Status.SUCCESS)
                context.startActivity(Intent.createChooser(shareIntent, context.resources
                    .getText(R.string.send_to)))
            }
        }
    }
}