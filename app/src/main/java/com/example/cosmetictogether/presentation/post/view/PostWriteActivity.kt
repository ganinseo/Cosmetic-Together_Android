package com.example.cosmetictogether.presentation.post.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.databinding.ActivityPostWriteBinding
import com.example.cosmetictogether.presentation.post.adapter.PostWriteAdapter
import com.example.cosmetictogether.presentation.post.viewmodel.PostWriteViewModel
import com.example.cosmetictogether.presentation.post.viewmodel.PostWriteViewModelFactory
import com.example.cosmetictogether.data.api.PostRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.api.RetrofitClient.postApi
import com.example.cosmetictogether.data.model.PostWriteRequest
import com.example.cosmetictogether.data.model.PostWriteResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostWriteActivity : AppCompatActivity() {

    private val viewModel: PostWriteViewModel by viewModels { PostWriteViewModelFactory(
        RetrofitClient.getInstance()) }
    private lateinit var binding: ActivityPostWriteBinding
    private val selectedImages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set RecyclerView Adapter with delete logic callback
        val adapter = PostWriteAdapter(selectedImages) { image ->
            // Handle the image deletion callback from the adapter
            deleteSelectedImage(image)
        }

        binding.imageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imageRecyclerView.adapter = adapter

        // Handle back button click
        binding.backButton.setOnClickListener { onBackPressed() }

        // Handle complete button click
        binding.completeButton.setOnClickListener {
            if (binding.postTextView.text.isNotEmpty() || selectedImages.isNotEmpty()) {
                postData()
            }
        }

        // Update complete button state based on text input and selected images
        binding.postTextView.addTextChangedListener {
            viewModel.updatePostText(it.toString())
            updateCompleteButtonState()
        }

        // Handle image select button click
        binding.imageSelectButton.setOnClickListener {
            openGallery()
        }

        // Observe ViewModel's live data
        viewModel.selectedImages.observe(this) { images ->
            selectedImages.clear()
            selectedImages.addAll(images)
            adapter.notifyDataSetChanged()
            updateCompleteButtonState()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        selectImageResult.launch(intent.toString())
    }

    private val selectImageResult = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris != null && uris.size <= 4) {
            // URIs를 String으로 변환하여 selectedImages에 추가
            val imageUris = uris.map { it.toString() }
            viewModel.updateSelectedImages(imageUris)
        } else {
            Toast.makeText(this, "최대 4개의 이미지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCompleteButtonState() {
        binding.completeButton.isEnabled = viewModel.isCompleteButtonEnabled()
    }

    // Handle image deletion in the activity
    private fun deleteSelectedImage(image: String) {
        selectedImages.remove(image)
        viewModel.updateSelectedImages(selectedImages)
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        val authToken = "Bearer $token"
        return authToken
    }

    private fun postData() {
        val request = PostWriteRequest(
            token = getToken(),
            images = selectedImages,
            request = binding.postTextView.text.toString()
        )

        postApi.postWrite(request).enqueue(object : retrofit2.Callback<PostWriteResponse> {
            override fun onResponse(call: retrofit2.Call<PostWriteResponse>, response: retrofit2.Response<PostWriteResponse>) {
                response.body()?.let {
                    if (it.status == 0) {
                        Toast.makeText(this@PostWriteActivity, "게시물 작성 완료!", Toast.LENGTH_SHORT).show()
                        navigateToPostActivity() // Moved navigation to a separate function
                    } else {
                        showErrorMessage(it.message)
                    }
                } ?: run {
                    showErrorMessage("Unexpected error")
                }
            }

            override fun onFailure(call: retrofit2.Call<PostWriteResponse>, t: Throwable) {
                Toast.makeText(this@PostWriteActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToPostActivity() {
        startActivity(Intent(this@PostWriteActivity, PostActivity::class.java))
        finish()
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }




}
