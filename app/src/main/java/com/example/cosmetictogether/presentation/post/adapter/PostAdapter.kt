package com.example.cosmetictogether.presentation.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.data.model.PostRecentResponse
import com.example.cosmetictogether.databinding.ItemPostBinding

class PostAdapter : ListAdapter<PostRecentResponse, PostAdapter.PostViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: PostRecentResponse) {
            binding.post = post
            binding.executePendingBindings()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PostRecentResponse>() {
        override fun areItemsTheSame(oldItem: PostRecentResponse, newItem: PostRecentResponse): Boolean {
            return oldItem.postTime == newItem.postTime // Assume unique time per post
        }

        override fun areContentsTheSame(oldItem: PostRecentResponse, newItem: PostRecentResponse): Boolean {
            return oldItem == newItem
        }
    }
}
