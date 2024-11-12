package com.example.cosmetictogether.presentation.post.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.databinding.ActivityPostBinding
import com.example.cosmetictogether.presentation.post.adapter.PostAdapter
import com.example.cosmetictogether.presentation.post.adapter.PostRepository
import com.example.cosmetictogether.presentation.post.viewmodel.PostViewModel
import com.example.cosmetictogether.presentation.post.viewmodel.PostViewModelFactory

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private val postRepository by lazy { PostRepository(RetrofitClient.postApi) }  // Use postApi here
    private val viewModel: PostViewModel by viewModels {
        PostViewModelFactory(postRepository)
    }
    private val postAdapter by lazy { PostAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 설정
        binding.postRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PostActivity)
            adapter = postAdapter
        }

        // 최신 글 가져오기
        viewModel.fetchPosts()
        viewModel.posts.observe(this, Observer { posts ->
            postAdapter.submitList(posts)
        })

        binding.createPostButton.setOnClickListener {
            startActivity(Intent(this, PostWriteActivity::class.java))
        }

        // BottomNavigationView의 항목 클릭 이벤트 설정
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_mypage -> {
                    startActivity(Intent(this, PostActivity::class.java))
                    true
                }
                R.id.action_form -> {
                    startActivity(Intent(this, PostActivity::class.java))
                    true
                }
                R.id.action_home -> {
                    startActivity(Intent(this, PostActivity::class.java))
                    true
                }
                R.id.action_search -> {
                    startActivity(Intent(this, PostActivity::class.java))
                    true
                }
                R.id.action_alarm -> {
                    startActivity(Intent(this, PostActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
