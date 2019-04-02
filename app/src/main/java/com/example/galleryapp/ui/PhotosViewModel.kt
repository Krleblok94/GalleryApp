package com.example.galleryapp.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.galleryapp.repository.PhotoRepository
import com.example.galleryapp.repository.model.Photo
import com.example.galleryapp.util.DateUtils
import com.example.galleryapp.util.GlideRequests

class PhotosViewModel(private val photoRepository: PhotoRepository) : ViewModel() {
    private val photoId = MutableLiveData<String>()

    // Since some images may be large, we add an indicator while the image is being prepared for sharing
    private val _shareStatus = MutableLiveData<Int>()
    val shareStatus: LiveData<Int>
        get() = _shareStatus

    val photos by lazy {
        photoRepository.loadPhotos()
    }

    val photo = Transformations.switchMap(photoId) {
        photoRepository.loadPhoto(it)
    }

    fun getPhotoById(id: String) {
        if (id != photoId.value) {
            photoId.value = id
        }
    }

    fun sharePhoto(context: Context, glide: GlideRequests) {
        photoRepository.sharePhoto(context, photo.value?.data, glide, _shareStatus)
    }

    fun savePhoto(path: String?, timestamp: Long?) {
        if (path != null && timestamp != null) {
            photoRepository.savePhoto(Photo(timestamp.toString(),
                "This photo was taken with the phone's camera", path,
                DateUtils.convertToStringFromTimestamp(timestamp), timestamp, true,
                "Photo #${photos.value?.data?.size?.plus(1)}"))
        }
    }
}