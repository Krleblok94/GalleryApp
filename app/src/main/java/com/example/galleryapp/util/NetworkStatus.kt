package com.example.galleryapp.util

data class NetworkStatus(val status: Int, val msg: String? = null) {
    companion object {
        fun success() = NetworkStatus(Constants.Status.SUCCESS)
        fun loading() = NetworkStatus(Constants.Status.LOADING)
        fun error(msg: String?) = NetworkStatus(Constants.Status.ERROR, msg)
    }
}