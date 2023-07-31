package com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppStoryRepository
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.GetAllStoryResponse
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiConfig
import com.irfannurrizki.panstoryapp.apphelper.AppInjection
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppMainViewModel (private val storyRepository: AppStoryRepository) : ViewModel() {

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStoryPaging(token).cachedIn(viewModelScope)

    fun uploadStory(
        token: String, imageMultipart: MultipartBody.Part, description: RequestBody,
        lat: RequestBody, lon: RequestBody
    ) =
//    ): LiveData<AppResult<AddNewStoryResponse>> =
        storyRepository.uploadStory(token, imageMultipart, description, lat, lon)
}


class AppMainViewModelFactory (private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AppMainViewModel::class.java) -> {
                AppMainViewModel(AppInjection.provideStoryRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }
    }


