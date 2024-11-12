package com.example.cosmetictogether.presentation.post.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.PostRetrofitInterface

class PostWriteViewModel(private val api: PostRetrofitInterface) : ViewModel() {

    private val _selectedImages = MutableLiveData<List<String>>()
    val selectedImages: LiveData<List<String>> = _selectedImages

    private val _postText = MutableLiveData<String>()
    val postText: LiveData<String> = _postText

    // Function to update post text
    fun updatePostText(text: String) {
        _postText.value = text
    }

    // Function to update selected images
    fun updateSelectedImages(images: List<String>) {
        _selectedImages.value = images
    }

    // Function to check if the complete button should be enabled
    fun isCompleteButtonEnabled(): Boolean {
        return !(_postText.value.isNullOrEmpty() && (_selectedImages.value?.isEmpty() ?: true))
    }
}
