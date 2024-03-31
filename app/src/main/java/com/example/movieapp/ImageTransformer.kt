package com.example.movieapp

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageTransformer {

    fun loadImageFromUrl(context: Context, imageUrl: String, imageView: ImageView) {
        Glide.with(context).load(imageUrl).into(imageView)
    }
}
