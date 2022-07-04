package com.susan.mystories

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ViewStoriesViewModel @Inject constructor(private val storiesAuth: StoriesAuth) : ViewModel() {
    val getMoreStories: LiveData<PagingData<ListStoriesPost>> = storiesAuth.allStoriesGet().cachedIn(viewModelScope)
    val getLocationStories: LiveData<List<ListStoriesPost>> = storiesAuth.pagingAllStories
    val loadAllData: LiveData<Boolean> = storiesAuth.showLoading
    val startPosting : LiveData<ResponseSignUpStories> = storiesAuth.responseStories

    fun postSatuStories(
        token: String,
        description: String,
        imgFile: File,
        location: LatLng
    ) {
        storiesAuth.postOneStory(token, description, imgFile, location)
    }

    fun getStoriesLoc(token: String) {
        storiesAuth.pagingStoriesGet(token)
    }

}