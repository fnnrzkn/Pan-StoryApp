package com.irfannurrizki.panstoryapp.appdata.apprepo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase.AppStoryDatabase
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.AddNewStoryResponse
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiService
import com.irfannurrizki.panstoryapp.appuserinterface.apppaging.AppStoryRemoteMediator
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class AppStoryRepository (
        private val appStoryDatabase: AppStoryDatabase,
        private val appApiService: AppApiService
) {
        fun getStoryPaging(token: String): LiveData<PagingData<ListStoryItem>> {
                @OptIn(ExperimentalPagingApi::class)
                return Pager(
                        config = PagingConfig(
                                pageSize = 5
                        ),
                        remoteMediator = AppStoryRemoteMediator(appApiService, appStoryDatabase, token),
                        pagingSourceFactory = {
//                AppStoryPagingSource(appApiService, token)
//                                appStoryDatabase.appStoryDao().getAllStoryPagingSource()
//                        }
                        appStoryDatabase.appStoryDao().getAllStory()
                        }
                ).liveData
        }

        fun uploadStory(token: String, imageMultipart: MultipartBody.Part, desc: RequestBody, lat: RequestBody, lon: RequestBody): LiveData<AppResult<AddNewStoryResponse>> = liveData{
                emit(AppResult.Loading)
                try {
                        val client = appApiService.addStory(desc, imageMultipart, token, lat, lon)
                        emit(AppResult.Success(client))
                }catch (e : Exception){
                        emit(AppResult.Error(e.message.toString()))
                }
        }
}