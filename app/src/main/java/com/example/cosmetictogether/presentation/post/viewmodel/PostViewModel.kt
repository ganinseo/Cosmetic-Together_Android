package com.example.cosmetictogether.presentation.post.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmetictogether.data.model.PostRecentResponse
import com.example.cosmetictogether.presentation.post.adapter.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    private val _posts = MutableLiveData<List<PostRecentResponse>>()
    val posts: LiveData<List<PostRecentResponse>> get() = _posts

    fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) { // Run network operation on background thread
            try {
                val posts = repository.getRecentPosts() // Call suspend function
                withContext(Dispatchers.Main) { // Switch back to the main thread to update UI
                    _posts.value = posts
                }
            } catch (e: Exception) {
                // Handle failure (network error, etc.)
                withContext(Dispatchers.Main) {
                    _posts.value = emptyList() // Optionally handle error
                }
            }
        }
    }
}
