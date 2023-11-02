package com.fiqri.mystoryapp.data.remote.retrofit

import com.fiqri.mystoryapp.data.remote.response.LoginResponse
import com.fiqri.mystoryapp.data.remote.response.RegisterResponse
import com.fiqri.mystoryapp.data.remote.response.StoryListResponse
import com.fiqri.mystoryapp.data.remote.response.StoryPostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") id: String,
        @Field("email") name: String,
        @Field("password") review: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") name: String,
        @Field("password") review: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStoriesPage(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryListResponse

    @GET("stories?location=1")
    fun getStoriesWithLocation(
    ): Call<StoryListResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): StoryPostResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStoryWithLocation(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody,
        @Part("lon") longitude: RequestBody,
    ): StoryPostResponse
}