package com.example.cosmetictogether.presentation.post.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmetictogether.data.api.PostRetrofitInterface
import com.example.cosmetictogether.data.model.PostWriteRequest
import com.example.cosmetictogether.data.model.PostWriteResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PostWriteViewModel(private val api: PostRetrofitInterface, private val context: Context) : ViewModel() {

    private val _selectedImages = MutableLiveData<List<String>>()
    val selectedImages: LiveData<List<String>> = _selectedImages

    private val _postText = MutableLiveData<String>()
    val postText: LiveData<String> = _postText

    private val _postWriteSuccess = MutableLiveData<Boolean>()
    val postWriteSuccess: LiveData<Boolean> = _postWriteSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val imageList = mutableListOf<String>()

    // 새롭게 추가된 부분 - 개별 이미지 URI 관리
    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> get() = _imageUri

    // 게시글 텍스트 업데이트
    fun updatePostText(text: String) {
        _postText.value = text
    }

    // 이미지 URI 설정 (단일 이미지 선택 시)
    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    // 선택된 이미지 추가
    fun addImage(imageUri: String) {
        imageList.add(imageUri)
        _selectedImages.value = imageList
    }

    // 선택된 이미지 제거
    fun removeImage(imageUri: String) {
        imageList.remove(imageUri)
        _selectedImages.value = imageList
    }

    // 선택된 이미지 목록 업데이트 (한 번에 여러 개)
    fun updateSelectedImages(images: List<String>) {
        imageList.clear()
        imageList.addAll(images)
        _selectedImages.value = imageList
    }

    // 완료 버튼 활성화 조건 체크
    fun isCompleteButtonEnabled(): Boolean {
        return !(_postText.value.isNullOrEmpty() && imageList.isEmpty())
    }

    fun postWrite(token: String, text: String, imageList: List<String>) {
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), "{\"request\": \"$text\"}")

        val imageParts = imageList.mapNotNull { imageUri ->
            val filePath = getRealPathFromURI(Uri.parse(imageUri))
            if (filePath != null) {
                val file = File(filePath)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", file.name, requestFile)
            } else null
        }

        api.postWrite(token, imageParts, requestBody).enqueue(object : Callback<PostWriteResponse> {
            override fun onResponse(call: Call<PostWriteResponse>, response: Response<PostWriteResponse>) {
                if (response.isSuccessful && response.body()?.status == 0) {
                    _postWriteSuccess.value = true
                } else {
                    _errorMessage.value = response.body()?.message ?: "게시글 작성 실패"
                }
            }

            override fun onFailure(call: Call<PostWriteResponse>, t: Throwable) {
                _errorMessage.value = "네트워크 오류: ${t.message}"
            }
        })
    }

    fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }
}
