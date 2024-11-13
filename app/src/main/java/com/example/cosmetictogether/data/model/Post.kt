package com.example.cosmetictogether.data.model

import com.google.gson.annotations.SerializedName
import org.w3c.dom.Comment
import retrofit2.http.Header

data class PostRecentResponse(
    @SerializedName("writerNickName") val writerNickName: String,
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("description") val description: String,
    @SerializedName("boardUrl") val boardUrl: List<String>,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("postTime") val postTime: String,
    @SerializedName("commentCount") val commentCount: Int
)

data class PostWriteRequest(
    @SerializedName("images") val images: List<String>? = null,
    @SerializedName("request") val request: String
)

data class PostWriteResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String
)

data class PostAfterEditResponse(
    @SerializedName("description") val description: String,
    @SerializedName("boardUrl") val boardUrl: List<String>
)

data class PostDetailResponse(
    @SerializedName("writerNickName") val writerNickName: String,
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("description") val description: String,
    @SerializedName("boardUrl") val boardUrl: List<String>,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("postTime") val postTime: String,
    @SerializedName("comments") val comments: List<Comment>
)

data class CommentRequest(
    @SerializedName("boardId") val boardId: Int,
    @SerializedName("comment") val comment: String
)

data class CommentResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String
)


data class PostDeleteResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String
)

data class PostEditRequest(
    @SerializedName("images") val images: List<String>? = null,
    @SerializedName("request") val request: RequestDetails
)

data class RequestDetails(
    @SerializedName("description") val description: String
)

data class PostEditResponse(
    @SerializedName("images") val images: List<String>,
    @SerializedName("request") val request: RequestDetails
)




