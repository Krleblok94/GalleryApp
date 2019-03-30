package com.example.galleryapp.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.galleryapp.R
import com.example.galleryapp.util.Constants
import com.example.galleryapp.util.DateUtils
import com.example.galleryapp.util.GlideRequests
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailActivity : AppCompatActivity() {

    private val photosViewModel by viewModel<PhotosViewModel>()
    private val glide by inject<GlideRequests> { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(toolbar)

        val photoId = intent.extras?.getString(Constants.EXTRA_PHOTO_ID)

        if (photoId != null) {
            photosViewModel.getPhotoById(photoId)
        }

        photosViewModel.photo.observe(this, Observer {
            if (it.data != null) {
                tvTitle.text = it.data.title
                tvComment.text = it.data.comment
                glide.load(it.data.picture).into(ivPhoto)
                val publishedAt = "Taken at ${DateUtils.formatDateString(it.data.publishedAt)}"
                tvPublishedAt.text = publishedAt
            }
        })

        photosViewModel.shareStatus.observe(this, Observer {
            shareProgressContainer.visibility = if (it == Constants.Status.LOADING)
                View.VISIBLE else View.GONE
        })
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                photosViewModel.sharePhoto(this, glide)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}