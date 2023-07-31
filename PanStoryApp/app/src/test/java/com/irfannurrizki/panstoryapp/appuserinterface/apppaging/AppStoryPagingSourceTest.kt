package com.irfannurrizki.panstoryapp.appuserinterface.apppaging

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem

internal class AppStoryPagingSourceTest : PagingSource<Int, LiveData<List<ListStoryItem>>>() {

    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}