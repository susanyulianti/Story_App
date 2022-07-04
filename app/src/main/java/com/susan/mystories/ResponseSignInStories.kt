package com.susan.mystories

import com.google.gson.annotations.SerializedName

data class ResponseSignInStories(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val loginResult: SignInStoriesResult
)

data class SignInStoriesResult(

    @SerializedName("name")
    val name: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("token")
    val token: String
)
