package com.example.cosmetictogether.presentation.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cosmetictogether.data.api.PostRetrofitInterface
import retrofit2.Retrofit

class PostWriteViewModelFactory(
    private val retrofit: Retrofit
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostWriteViewModel::class.java)) {
            val api = retrofit.create(PostRetrofitInterface::class.java)
            return PostWriteViewModel(api) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
