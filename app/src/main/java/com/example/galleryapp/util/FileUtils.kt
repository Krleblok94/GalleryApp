package com.example.galleryapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {

    var currentPhotoPath: String? = null
    var timestamp: Long? = null

    fun sendTakePictureIntent(activity: Activity) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity.packageManager)?.also {
                val photoFile: File? = try {
                    FileUtils.createImageFile(activity)
                } catch (ex: IOException) {
                    null
                }

                photoFile?.also {
                    val photoUri: Uri = FileProvider.getUriForFile(activity,
                        "com.example.galleryapp.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    activity.startActivityForResult(takePictureIntent, Constants.REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    // This is used to get the uri if the photo is taken by the user and the image file is already in the
    // device's memory
    fun getLocalBitmapUri(context: Context, path: String) : Uri =
        FileProvider.getUriForFile(context, "com.example.galleryapp.fileprovider", File(path))

    // For url related images, the photo first has to be downloaded to a local temporary file to create a uri
    // for sharing
    fun getUrlBitmapUri(context: Context, bitmap: Bitmap) : Uri {
        timestamp = System.currentTimeMillis()
        val file = File.createTempFile("image_${timestamp}_",".jpg", context.filesDir)
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream)
        fileOutputStream.close()
        return FileProvider.getUriForFile(context, "com.example.galleryapp.fileprovider", file)
    }

    // Creates a temporary file to save the image returned by the camera
    private fun createImageFile(context: Context) : File {
        timestamp = System.currentTimeMillis()
        return File.createTempFile("image_${timestamp}_",".jpg", context.filesDir)
            .apply {
            currentPhotoPath = absolutePath
        }
    }
}