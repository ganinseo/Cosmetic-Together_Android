package com.example.cosmetictogether.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.cosmetictogether.BuildConfig

object RetrofitClient { // Retrofit 설정 담당 객체
    //private const val BASE_URL = "http://10.0.2.2:8080/"
    private const val BASE_URL = " https://bab0-125-143-190-122.ngrok-free.app"

    private val retrofit: Retrofit by lazy {
        // 로깅 인터셉터 추가
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY // 디버그 모드일 때는 BODY 레벨로 설정
            } else {
                HttpLoggingInterceptor.Level.HEADERS // 릴리스 모드에서는 HEADERS 레벨로 설정
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging) // OkHttpClient에 로깅 인터셉터 추가
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val postApi: PostRetrofitInterface by lazy {
        retrofit.create(PostRetrofitInterface::class.java)
    }

    val apiService: AuthRetrofitInterface by lazy {
        retrofit.create(AuthRetrofitInterface::class.java)
    }

    fun getInstance(): Retrofit {
        return retrofit
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}
