package com.example.galleryapp.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryapp.R
import com.example.galleryapp.repository.model.Photo
import com.example.galleryapp.util.Constants
import com.example.galleryapp.util.GlideRequests

class PhotoAdapter(private val glide: GlideRequests) : RecyclerView.Adapter<PhotoViewHolder>() {

    private val photoList = mutableListOf<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent,
                false))
    }

    override fun getItemCount(): Int = photoList.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photoList[position]
        glide.load(photo.picture).into(holder.ivPhoto)
        holder.tvTitle.text = photo.title

        holder.ivPhoto.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_PHOTO_ID, photo.id)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    fun addList(photoList: List<Photo>) {
        this.photoList.clear()
        this.photoList.addAll(photoList)
        notifyDataSetChanged()
    }
}