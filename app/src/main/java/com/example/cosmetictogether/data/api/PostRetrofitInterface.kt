package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.CommentRequest
import com.example.cosmetictogether.data.model.CommentResponse
import com.example.cosmetictogether.data.model.PostWriteRequest
import com.example.cosmetictogether.data.model.PostWriteResponse
import com.example.cosmetictogether.data.model.PostAfterEditResponse
import com.example.cosmetictogether.data.model.PostDeleteResponse
import com.example.cosmetictogether.data.model.PostDetailResponse
import com.example.cosmetictogether.data.model.PostEditRequest
import com.example.cosmetictogether.data.model.PostEditResponse
import com.example.cosmetictogether.data.model.PostRecentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface PostRetrofitInterface {
    @Multipart
    @POST("/api/v1/board")
    fun postWrite(
        @Header("Authorization") token: String,
        @Part images: List<MultipartBody.Part>,
        @Part("request") request: RequestBody
    ): Call<PostWriteResponse>

    @GET("/api/v1/board/recent") // 최신 글 조회
    fun postRecent(): Call<List<PostRecentResponse>>


    @GET("/api/v1/board/info/{boardId}") // 수정 후 출력
    fun postAfterEdit(@Path("boardId") boardId: Long): Call<PostAfterEditResponse>

    @GET("/api/v1/board/{boardId}") // 글 조회
    fun postDetail(@Path("boardId") boardId: Long): Call<PostDetailResponse>

    @DELETE("/api/v1/board/{boardId}") // 글 삭제
    fun postDelete(@Path("boardId") boardId: Long): Call<PostDeleteResponse>

    @PATCH("/api/v1/board/{boardId}") // 글 수정
    fun PostEdit(
        @Path("boardId") boardId: Long,
        @Body request: PostEditRequest
    ): Call<PostEditResponse>

    @POST("/api/v1/comment") // 댓글
    fun postComment(
        @Body request: CommentRequest
    ): Call<CommentResponse>

}