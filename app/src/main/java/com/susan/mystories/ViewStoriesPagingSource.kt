package com.susan.mystories

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.first
import java.lang.Exception
import javax.inject.Inject

class ViewStoriesPagingSource @Inject constructor(private val apiStories: ApiStories, private val preferencesStoriesAccount: PreferencesStoriesAccount) : PagingSource<Int, ListStoriesPost>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoriesPost> {
        return try {
            val position = params.key ?: INITIAL_PAGE
            val token = preferencesStoriesAccount.storiesAccountGet().first().token
            setStoriesIdlingResource {
                if (token.trim().isNotEmpty()) {
                    Log.d("token", "load: $token")
                    val responseData =
                        apiStories.getViewStoriesMore("Bearer $token", position, params.loadSize)
                    Log.d("tag", "load: ${responseData.message()}")
                    if (responseData.isSuccessful) {
                        Log.d("token", "load no error: ${responseData.body()}")
                        LoadResult.Page(
                            data = responseData.body()?.listStory ?: emptyList(),
                            prevKey = if (position == 1) null else position - 1,
                            nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
                        )
                    } else {
                        Log.d("token", "load Error: $token")
                        LoadResult.Error(Exception("fail"))
                    }
                } else {
                    Log.d("tag", "load: Data Error2")
                    LoadResult.Error(Exception("Gagal"))
                }
            }
        } catch (e: Exception) {
            Log.d("exception", "load: Error ${e.message}")
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoriesPost>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE = 1
    }
}