package com.example.cosmetictogether.presentation.post.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.data.api.PostRetrofitInterface
import com.example.cosmetictogether.databinding.ActivityPostWriteBinding
import com.example.cosmetictogether.presentation.post.adapter.PostWriteAdapter
import com.example.cosmetictogether.presentation.post.viewmodel.PostWriteViewModel
import com.example.cosmetictogether.presentation.post.viewmodel.PostWriteViewModelFactory
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.PostWriteResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PostWriteActivity : AppCompatActivity() {

    private val viewModel: PostWriteViewModel by viewModels {
        val apiService = RetrofitClient.getInstance().create(PostRetrofitInterface::class.java)
        PostWriteViewModelFactory(apiService, this@PostWriteActivity)
    }

    private lateinit var binding: ActivityPostWriteBinding
    private val selectedImages = mutableListOf<String>()
    private val REQUEST_CODE_PERMISSION = 1001

    // ActivityResultLauncher를 사용해 이미지 선택
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris != null && uris.size <= 4) {
            val imageUris = uris.map { it.toString() }
            viewModel.updateSelectedImages(imageUris)
            uris.forEach { uri -> scanMediaFile(this, uri) }
        } else {
            Toast.makeText(this, "최대 4개의 이미지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 설정
        val adapter = PostWriteAdapter(selectedImages) { image ->
            deleteSelectedImage(image)
        }
        binding.imageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imageRecyclerView.adapter = adapter

        // 뒤로 가기 버튼
        binding.backButton.setOnClickListener { onBackPressed() }

        // 완료 버튼 클릭 시 데이터 전송
        binding.completeButton.setOnClickListener {
            if (binding.postTextView.text.isNotEmpty() || selectedImages.isNotEmpty()) {
                postData()
            }
        }

        // 텍스트 입력 변경 감지
        binding.postTextView.addTextChangedListener {
            viewModel.updatePostText(it.toString())
            updateCompleteButtonState()
        }

        // 이미지 선택 버튼
        binding.imageSelectButton.setOnClickListener {
            checkAndOpenGallery()
        }

        // ViewModel의 LiveData 관찰
        viewModel.selectedImages.observe(this) { images ->
            selectedImages.clear()
            selectedImages.addAll(images)
            adapter.notifyDataSetChanged()
            updateCompleteButtonState()
        }
    }

    private fun checkAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
            } else {
                openGallery()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            } else {
                openGallery()
            }
        }
    }

    private fun requestPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "저장소 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        imagePickerLauncher.launch("image/*")
    }

    private fun updateCompleteButtonState() {
        binding.completeButton.isEnabled = viewModel.isCompleteButtonEnabled()
    }

    private fun deleteSelectedImage(image: String) {
        selectedImages.remove(image)
        viewModel.updateSelectedImages(selectedImages)
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)?.let { "Bearer $it" } ?: ""
    }

    private fun postData() {
        val token = getToken()
        if (token.isEmpty()) {
            Toast.makeText(this, "토큰이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val postText = binding.postTextView.text.toString()
        val imageUris = selectedImages.map { Uri.parse(it) }

        // 이미지 파일 준비
        val imageParts = imageUris.mapNotNull { uri ->
            val filePath = getRealPathFromURI(uri)
            if (filePath != null) {
                val file = File(filePath)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", file.name, requestFile)
            } else null
        }

        // PostWriteRequest를 JSON 형태의 RequestBody로 변환
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            "{\"request\": \"$postText\"}"
        )

        // Retrofit API 호출
        val call = RetrofitClient.postApi.postWrite(token, imageParts, requestBody)
        call.enqueue(object : Callback<PostWriteResponse> {
            override fun onResponse(call: Call<PostWriteResponse>, response: Response<PostWriteResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PostWriteActivity, "게시물 작성 완료!", Toast.LENGTH_SHORT).show()
                    navigateToPostActivity(postText, selectedImages)
                } else {
                    showErrorMessage(response.errorBody()?.string() ?: "서버 오류 발생")
                }
            }

            override fun onFailure(call: Call<PostWriteResponse>, t: Throwable) {
                Toast.makeText(this@PostWriteActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return cursor.getString(columnIndex)
            }
        }
        return null
    }

    private fun navigateToPostActivity(description: String, images: List<String>) {
        val intent = Intent(this, PostActivity::class.java).apply {
            putExtra("description", description)
            putStringArrayListExtra("images", ArrayList(images))
        }
        startActivity(intent)
        finish()
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun scanMediaFile(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = uri
        context.sendBroadcast(intent)
    }
}
