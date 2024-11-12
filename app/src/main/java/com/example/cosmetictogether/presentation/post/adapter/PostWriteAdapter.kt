package com.example.cosmetictogether.presentation.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.databinding.ItemPostWritePhotoBinding

class PostWriteAdapter(
    private val selectedImages: MutableList<String>,
    private val onImageDeleted: (String) -> Unit // Callback for when an image is deleted
) : RecyclerView.Adapter<PostWriteAdapter.PostWriteViewHolder>() {

    inner class PostWriteViewHolder(private val binding: ItemPostWritePhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUri: String) {
            binding.selectedImageView.setImageURI(android.net.Uri.parse(imageUri))  // Display selected image

            // Set up delete button
            binding.deleteImageButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedImages.removeAt(position)  // Remove image from the list
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, selectedImages.size)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostWriteViewHolder {
        val binding = ItemPostWritePhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostWriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostWriteViewHolder, position: Int) {
        holder.bind(selectedImages[position])
    }

    override fun getItemCount(): Int = selectedImages.size

    fun submitList(images: List<String>) {
        selectedImages.clear()
        selectedImages.addAll(images)
        notifyDataSetChanged()
    }
}
