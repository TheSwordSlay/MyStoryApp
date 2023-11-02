package com.fiqri.mystoryapp.helper.stories

import com.fiqri.mystoryapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryList(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..20) {
            val story = ListStoryItem(
                "https://static.wikia.nocookie.net/p__/images/f/fd/PomniTransparent.webp/revision/latest?cb=20231015112601&path-prefix=protagonist",
                "2022",
                "story $i",
                "dfjhjhjhjs",
                "$i",
                "$i",
                "$i"
            )
            items.add(story)
        }
        return items
    }
}