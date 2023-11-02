package com.fiqri.mystoryapp.helper.stories

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fiqri.mystoryapp.data.remote.response.ListStoryItem
import com.fiqri.mystoryapp.data.remote.retrofit.ApiService

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {

        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStoriesPage(position, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory!!,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else position + 1
            )

        } catch (exception: Exception) {
            Log.v("gagal cuy", exception.toString())
            return LoadResult.Error(exception)
        }
    }
}

