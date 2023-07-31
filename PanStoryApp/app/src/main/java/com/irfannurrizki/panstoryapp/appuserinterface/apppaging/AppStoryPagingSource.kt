package com.irfannurrizki.panstoryapp.appuserinterface.apppaging

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiService

class AppStoryPagingSource (private val appApiService: AppApiService, private val token: String) : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = appApiService.getAllStory(token, page, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
}
