package com.example.galleryapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryapp.R
import com.example.galleryapp.util.Constants.REQUEST_TAKE_PHOTO
import com.example.galleryapp.util.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    private val photosViewModel by viewModel<PhotosViewModel>()
    private val photoAdapter by inject<PhotoAdapter> { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        photosList.layoutManager = GridLayoutManager(this, 2)
        photosList.adapter = photoAdapter

        photosViewModel.photos.observe(this, Observer {

            if (it.data != null && !it.data.isEmpty()) {
                tvEmpty.visibility = View.GONE
                photosList.visibility = View.VISIBLE
                photoAdapter.addList(it.data)
            } else {
                tvEmpty.visibility = View.VISIBLE
                photosList.visibility = View.GONE
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            photosViewModel.savePhoto(FileUtils.currentPhotoPath, FileUtils.timestamp)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_take_photo -> {
                FileUtils.sendTakePictureIntent(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
