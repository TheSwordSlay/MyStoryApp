package com.fiqri.mystoryapp.helper.stories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fiqri.mystoryapp.data.local.StoryRepository
import com.fiqri.mystoryapp.data.remote.response.ListStoryItem
import com.fiqri.mystoryapp.data.remote.response.StoryListResponse
import com.fiqri.mystoryapp.data.remote.retrofit.ApiConfig
import com.fiqri.mystoryapp.di.Injection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(storyRepository: StoryRepository) : ViewModel()  {

    private val _listDataStory = MutableLiveData<List<ListStoryItem>>()
    val listDataStory: LiveData<List<ListStoryItem>> = _listDataStory

    val storyList: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory().cachedIn(viewModelScope)

    fun getStoryWithLocationList(token: String) {
        val client = ApiConfig.getApiService(token).getStoriesWithLocation()
        client.enqueue(object : Callback<StoryListResponse> {
            override fun onResponse(
                call: Call<StoryListResponse>,
                response: Response<StoryListResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listDataStory.value = response.body()?.listStory!!
                    }
                }
            }
            override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {

            }
        })
    }

}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}