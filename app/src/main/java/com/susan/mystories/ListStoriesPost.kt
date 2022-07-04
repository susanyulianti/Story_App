package com.susan.mystories

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListStoriesPost(

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double,

    @SerializedName("name")
    val name: String,

    @SerializedName("photoUrl")
    val photoUrl: String

):Parcelable
