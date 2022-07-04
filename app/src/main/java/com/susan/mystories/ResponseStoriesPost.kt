package com.susan.mystories

import com.google.gson.annotations.SerializedName

data class ResponseStoriesPost(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<ListStoriesPost>
)
