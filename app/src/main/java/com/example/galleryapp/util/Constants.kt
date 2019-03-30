package com.example.galleryapp.util

object Constants {

    const val BASE_URL = "http://www.json-generator.com/api/json/get/"
    const val END_POINT = "cftPFNNHsi"
    const val TIMEOUT_IN_SEC = 15L

    const val API_RATE_LIMITER = "apiRateLimiter"

    const val REQUEST_TAKE_PHOTO = 1

    const val EXTRA_PHOTO_ID = "extraPhotoId"

    // Status codes for network bound resource
    object Status {
        const val SUCCESS = 1
        const val LOADING = 0
        const val ERROR = -1
    }
}