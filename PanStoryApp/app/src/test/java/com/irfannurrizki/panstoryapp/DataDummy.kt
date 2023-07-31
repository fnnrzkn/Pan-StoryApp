package com.irfannurrizki.panstoryapp

import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.AddNewStoryResponse
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem

object DataDummy {
    fun generateDummyStoriesResponse(): AddNewStoryResponse {
        val stories = arrayListOf<ListStoryItem>()

        for (i in 0 until 10){
            val story = ListStoryItem(
                "story-4stHTSLEi9jy1rC5",
                "fanfan",
                "asdffghjkl",
                "https://story-api.dicoding.dev/images/stories/photos-2345217264593_jK4lcjg6.jpg",
                "2023-11-05T12:40:53.284Z",
                (-7.7741421),
                (118.2476194)
            )
            stories.add(story)
        }
        return AddNewStoryResponse(false,"Stories fetched successfully",stories)
    }

    fun generateDummyStoriesList(): ArrayList<ListStoryItem> {
        val stories = arrayListOf<ListStoryItem>()

        for (i in 0 until 5){
            val story = ListStoryItem(
                "story-4stHTSLEi9jy1rC5",
                "fanfan",
                "asdffghjkl",
                "https://story-api.dicoding.dev/images/stories/photos-2345217264593_jK4lcjg6.jpg",
                "2023-11-05T12:40:53.284Z",
                (-7.7741421),
                (118.2476194)
            )
            stories.add(story)
        }
        return stories
    }
    fun emptyListStoryDummy(): List<ListStoryItem> = emptyList()

}