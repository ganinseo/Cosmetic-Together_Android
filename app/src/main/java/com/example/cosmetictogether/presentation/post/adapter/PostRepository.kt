package com.example.cosmetictogether.presentation.post.adapter

import com.example.cosmetictogether.data.api.PostRetrofitInterface
import com.example.cosmetictogether.data.model.PostRecentResponse
import retrofit2.Call
import retrofit2.await

class PostRepository(
    private val apiService: PostRetrofitInterface
) {
    // Suspend function to fetch recent posts asynchronously
    suspend fun getRecentPosts(): List<PostRecentResponse> {
        return apiService.postRecent().await()
    }
}
