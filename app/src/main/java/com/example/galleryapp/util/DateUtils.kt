package com.example.galleryapp.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    @SuppressLint("ConstantLocale")
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss z", Locale.getDefault())

    fun convertToStringFromTimestamp(timestamp: Long) : String = simpleDateFormat.format(Date(timestamp))

    fun convertToTimestampFromString(dateString: String) : Long = simpleDateFormat.parse(dateString).time

    fun formatDateString(dateString: String) : String {
        val sdfOut = SimpleDateFormat("hh:mm 'on' EEE, MMM d, yyyy", Locale.getDefault())
        return sdfOut.format(simpleDateFormat.parse(dateString))
    }
}