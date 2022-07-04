package com.susan.mystories

import com.google.gson.annotations.SerializedName

data class ResponseSignUpStories(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String

)
