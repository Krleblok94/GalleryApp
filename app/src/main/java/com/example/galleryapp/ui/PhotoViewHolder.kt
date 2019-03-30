package com.example.galleryapp.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryapp.R

class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivPhoto: ImageView = view.findViewById(R.id.ivPhoto)
    val tvTitle: TextView = view.findViewById(R.id.tvTitle)
}