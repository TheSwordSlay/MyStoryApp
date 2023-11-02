package com.fiqri.mystoryapp.data.local

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.fiqri.mystoryapp.data.remote.response.ListStoryItem
import com.fiqri.mystoryapp.data.remote.retrofit.ApiService
import com.fiqri.mystoryapp.helper.stories.StoryPagingSource

class StoryRepository(private val apiService: ApiService) {

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }
}
