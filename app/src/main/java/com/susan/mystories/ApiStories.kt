package com.susan.mystories

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiStories {

    @POST("register")
    fun accountStoriesCreated(@Body signUp : ModelSignUp):Call<ResponseSignUpStories>

    @POST("login")
    fun accountStoriesSignIn(@Body signIn : ModelSignInStories):Call<ResponseSignInStories>

    @GET("stories")
    suspend fun getViewStoriesMore(@Header("Authorization") auth: String, @Query("page") page: Int, @Query("size") size: Int): Response<ResponseStoriesPost>

    @GET("stories?location=1")
    fun getLocation(@Header("Authorization") auth: String, @Query("location")location: Int = 1): Call<ResponseStoriesPost>

    @Multipart
    @POST("stories")
    fun uploadOneStories(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
    ): Call<ResponseSignUpStories>
}