package com.susan.mystories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class StoriesAuth  @Inject constructor(private val apiStories: ApiStories, private val pagingSource: ViewStoriesPagingSource){

    private val _loadStories = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _loadStories
    private val _responseStories = MutableLiveData<ResponseSignUpStories>()
    val responseStories: LiveData<ResponseSignUpStories> = _responseStories
    private val _pagingStories = MutableLiveData<List<ListStoriesPost>>()
    val pagingAllStories: LiveData<List<ListStoriesPost>> = _pagingStories

    fun pagingStoriesGet(token: String) {
        val client = apiStories.getLocation("Bearer $token")
        client.enqueue(object : Callback<ResponseStoriesPost> {
            override fun onResponse(
                call: Call<ResponseStoriesPost>,
                response: Response<ResponseStoriesPost>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _pagingStories.value = responseBody.listStory
                }
            }

            override fun onFailure(call: Call<ResponseStoriesPost>, t: Throwable) {
                Log.e("ResponseError", "onFailure: ${t.message}")
            }
        })
    }

    fun allStoriesGet(): LiveData<PagingData<ListStoriesPost>> {
        setStoriesIdlingResource {
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                pagingSourceFactory = {
                    pagingSource
                }
            ).liveData
        }
    }

    fun postOneStory(token: String, description: String, imgFile: File, location: LatLng) {
        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val requestImage = imgFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val requestBodyLat = location.latitude.toString().toRequestBody("text/plain".toMediaType())
        val requestBodyLon = location.longitude.toString().toRequestBody("text/plain".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imgFile.name,
            requestImage
        )
        _loadStories.value = true
        val client = apiStories.uploadOneStories(imageMultipart, requestDescription, "Bearer $token",requestBodyLat,requestBodyLon)
        client.enqueue (object : Callback<ResponseSignUpStories> {
            override fun onResponse(call: Call<ResponseSignUpStories>, response: Response<ResponseSignUpStories>) {
                if (response.isSuccessful && response.body() != null) {
                    _responseStories.value = response.body()
                    _loadStories.value = false
                }
            }

            override fun onFailure(call: Call<ResponseSignUpStories>, t: Throwable) {
                Log.e("ResponseError", "onFailure: ${t.message}")
                _loadStories.value = false
            }
        })
    }
}