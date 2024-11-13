package com.example.cosmetictogether.presentation.post.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.PostRecentResponse
import com.example.cosmetictogether.databinding.ActivityPostBinding
import com.example.cosmetictogether.presentation.post.adapter.PostAdapter
import com.example.cosmetictogether.presentation.post.adapter.PostRepository
import com.example.cosmetictogether.presentation.post.viewmodel.PostViewModel
import com.example.cosmetictogether.presentation.post.viewmodel.PostViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private val postRepository by lazy { PostRepository(RetrofitClient.postApi) }
    private val viewModel: PostViewModel by viewModels {
        PostViewModelFactory(postRepository)
    }
    private val postAdapter by lazy { PostAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        handleIntentData()
        observeViewModel()
        setupCreatePostButton()
        setupBottomNavigationView()
    }

    private fun setupRecyclerView() {
        binding.postRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PostActivity)
            adapter = postAdapter
        }
    }

    // 새로운 게시글 데이터 처리
    private fun handleIntentData() {
        val newPostDescription = intent.getStringExtra("description")
        val newPostImages = intent.getStringArrayListExtra("images")

        if (!newPostDescription.isNullOrEmpty() && !newPostImages.isNullOrEmpty()) {
            val newPost = PostRecentResponse(
                writerNickName = "작성자",
                profileUrl = "",
                description = newPostDescription,
                boardUrl = newPostImages,
                likeCount = 0,
                postTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date()),
                commentCount = 0
            )

            // ViewModel을 통해 새로운 게시글 추가
            viewModel.addNewPost(newPost)
        }
    }

    // ViewModel 관찰
    private fun observeViewModel() {
        viewModel.fetchPosts()
        viewModel.posts.observe(this) { posts ->
            // 새로운 리스트를 생성하여 전달
            postAdapter.submitList(posts.toList())
        }
    }

    private fun setupCreatePostButton() {
        binding.createPostButton.setOnClickListener {
            startActivity(Intent(this, PostWriteActivity::class.java))
        }
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_mypage -> {
                    navigateToActivity(PostActivity::class.java)
                    true
                }
                R.id.action_form -> {
                    navigateToActivity(PostActivity::class.java)
                    true
                }
                R.id.action_home -> {
                    navigateToActivity(PostActivity::class.java)
                    true
                }
                R.id.action_search -> {
                    navigateToActivity(PostActivity::class.java)
                    true
                }
                R.id.action_alarm -> {
                    navigateToActivity(PostActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
