package com.fiqri.mystoryapp.data.remote.response

data class StoryListResponse(
	val listStory: List<ListStoryItem>? = null,
	val error: Boolean? = null,
	val message: String? = null
)

data class ListStoryItem(
	val photoUrl: String? = null,
	val createdAt: String? = null,
	val name: String? = null,
	val description: String? = null,
	val lon: Any? = null,
	val id: String? = null,
	val lat: Any? = null
)

