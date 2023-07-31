package com.irfannurrizki.panstoryapp.apphelper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppMainRepository
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppStoryRepository
import com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase.AppStoryDatabase
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiConfig
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiService

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
object AppInjection{
    fun provideStoryRepository(context: Context): AppStoryRepository {
        val database = AppStoryDatabase.getDatabase(context)
        val appApiService = AppApiConfig.getAppApiService()
        return AppStoryRepository(database, appApiService)
    }

    fun provideUserRepository(context: Context): AppMainRepository {
        val appApiService = AppApiConfig.getAppApiService()
        return AppMainRepository.getInstance(context.dataStore, appApiService)
    }
}

