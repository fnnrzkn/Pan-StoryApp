package com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.paging.PagingSource
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem

@Dao
interface AppStoryDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertStory(story: List<ListStoryItem>)

        @Query("SELECT * FROM storydata")
        fun getAllStory(): PagingSource<Int, ListStoryItem>

        @Query("DELETE FROM storydata")
        suspend fun deleteAll()
    }
//    @Query("SELECT * FROM StoryData")
//    fun getAllStoryLiveData(): LiveData<List<ListStoryItem>>
//
//    @Query("SELECT * FROM StoryData")
//    fun getLocation(): LiveData<List<ListStoryItem>>
//
//    @Query("DELETE FROM StoryData")
//    suspend fun deleteAll()
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertStory(story: List<ListStoryItem>)
//
//    @Query("SELECT * FROM StoryData")
//    fun getAllStoryPagingSource(): PagingSource<Int,ListStoryItem>
//

