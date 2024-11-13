package com.example.cosmetictogether.presentation.post.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cosmetictogether.data.api.PostRetrofitInterface

class PostWriteViewModelFactory(
    private val api: PostRetrofitInterface,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostWriteViewModel::class.java)) {
            return PostWriteViewModel(api, context) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
