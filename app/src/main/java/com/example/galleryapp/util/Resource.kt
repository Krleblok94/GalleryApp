package com.example.galleryapp.util

data class Resource<out T>(val data: T?, val networkStatus: NetworkStatus) {
    companion object {
        fun <T> success(data: T?): Resource<T> = Resource(data, NetworkStatus.success())

        fun <T> error(msg: String?, data: T?): Resource<T> = Resource(data, NetworkStatus.error(msg))

        fun <T> loading(data: T?): Resource<T> = Resource(data, NetworkStatus.loading())
    }
}