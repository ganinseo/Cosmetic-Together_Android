package com.example.cosmetictogether.presentation.post.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.cosmetictogether.R
import java.text.SimpleDateFormat
import java.util.*

object BindingAdapters {
    @BindingAdapter("uploadTime")
    fun TextView.setUploadTime(postTime: String) {
        // Parse the postTime string into a Date object (assuming it's in ISO 8601 format)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val postDate = dateFormat.parse(postTime)

        if (postDate != null) {
            val currentTime = Date()

            // Calculate the difference in time between now and the post time
            val diffInMillis = currentTime.time - postDate.time
            val diffInMinutes = diffInMillis / 60000 // Convert to minutes

            // Display a relative time, e.g., "5 minutes ago"
            val formattedTime = when {
                diffInMinutes < 1 -> "Just now"
                diffInMinutes < 60 -> "$diffInMinutes minutes ago"
                diffInMinutes < 1440 -> "${diffInMinutes / 60} hours ago"
                else -> "${diffInMinutes / 1440} days ago"
            }

            // Set the text of the TextView
            text = formattedTime
        }
    }

    @JvmStatic
    @BindingAdapter("srcCompat")
    fun loadImage(imageView: ImageView, imageUrl: String?) {
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .into(imageView)
        } else {
            // Optionally load a placeholder image if URL is null or empty
            Glide.with(imageView.context)
                .load(imageUrl) // Replace with your placeholder image
                .into(imageView)
        }
    }
}