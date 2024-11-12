package com.example.cosmetictogether.presentation.post.adapter

import com.example.cosmetictogether.data.api.PostRetrofitInterface
import com.example.cosmetictogether.data.model.PostRecentResponse
import retrofit2.Call

class PostRepository(
    private val apiService: PostRetrofitInterface
) {
    // Suspend function to fetch recent posts asynchronously
    suspend fun getRecentPosts(): List<PostRecentResponse> {
        return apiService.postRecent() // This is a suspend function, no need for Call
    }
}
