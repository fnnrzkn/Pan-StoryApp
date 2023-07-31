package com.irfannurrizki.panstoryapp.appuserinterface.apppaging

import androidx.paging.ExperimentalPagingApi
import androidx.room.withTransaction
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase.AppRemoteKeys
import com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase.AppStoryDatabase
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiService

@OptIn(ExperimentalPagingApi::class)
class AppStoryRemoteMediator(
    private val apiService: AppApiService,
    private val database: AppStoryDatabase,
    private val token: String

): RemoteMediator<Int, ListStoryItem>(){
    override suspend fun load(loadType: LoadType, state: PagingState<Int, ListStoryItem>): MediatorResult {
        val page = when (loadType) {
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
        }

        try {
            val responseData = apiService.getAllStory(token, page, state.config.pageSize).listStory
            val endOfPaginationReached = responseData.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH){
                    database.appRemoteKeysDao().deleteRemoteKeys()
                    database.appStoryDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    AppRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                database.appRemoteKeysDao().insertAll(keys)
                database.appStoryDao().insertStory(responseData)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): AppRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.appRemoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): AppRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.appRemoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryItem>): AppRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.appRemoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
