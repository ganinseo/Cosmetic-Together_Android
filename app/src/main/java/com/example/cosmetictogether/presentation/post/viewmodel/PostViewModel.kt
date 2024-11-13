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

class PostViewModel(private val repository: PostRepository) : ViewModel() {
    private val _posts = MutableLiveData<List<PostRecentResponse>>()
    val posts: LiveData<List<PostRecentResponse>> get() = _posts

    fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val posts = repository.getRecentPosts()
                withContext(Dispatchers.Main) {
                    _posts.value = posts
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _posts.value = emptyList()
                }
            }
        }
    }

    fun addNewPost(newPost: PostRecentResponse) {
        val currentPosts = _posts.value?.toMutableList() ?: mutableListOf()
        currentPosts.add(0, newPost)
        _posts.value = currentPosts
    }
}
