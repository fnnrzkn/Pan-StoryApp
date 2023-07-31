package com.irfannurrizki.panstoryapp.appdata.apprepo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase.AppUserModel
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.GetAllStoryResponse
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.LoginResponse
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.RegisterResponse
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppMainRepository (
    private val dataStore: DataStore<Preferences>,
    private val appApiService: AppApiService
){

    fun register(name: String, email: String, password: String): LiveData<AppResult<RegisterResponse>> = liveData {
        emit(AppResult.Loading)
        try {
            val result = appApiService.addUser(name, email, password)
            emit(AppResult.Success(result))
        } catch (exception: Exception){
            emit(AppResult.Error(exception.message.toString()))
        }
    }

    fun login(email: String, password: String) : LiveData<AppResult<LoginResponse>> = liveData {
        emit(AppResult.Loading)
        try {
            val result = appApiService.getUser(email,password)
            emit(AppResult.Success(result))
        } catch (exception: Exception){
            emit(AppResult.Error(exception.message.toString()))
        }
    }

    fun getLocationStoriesMap(token: String) : LiveData<AppResult<GetAllStoryResponse>> = liveData {
        emit(AppResult.Loading)
        try {
            val result = appApiService.getStoriesMapsLocation(token)
             emit(AppResult.Success(result))
        } catch (exception: Exception){
            emit(AppResult.Error(exception.message.toString()))
        }
    }

    fun getUser(): Flow<AppUserModel> {
        return dataStore.data.map { preferences ->
            AppUserModel(
                preferences[USER_ID_KEY] ?:"",
                preferences[NAME_KEY] ?:"",
                preferences[TOKEN_KEY] ?:"",
                preferences[LOGIN_KEY] ?:false
            )
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun saveUser(user: AppUserModel){
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.userId
            preferences[NAME_KEY] = user.name
            preferences[TOKEN_KEY] = user.token
            preferences[LOGIN_KEY] = user.isLogin
        }
    }

    fun isLogin() : Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_KEY] ?: false
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = false
        }
    }

    suspend fun login(){
        dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = true
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: AppMainRepository? = null

        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val LOGIN_KEY = booleanPreferencesKey("state")

        fun getInstance(
            dataStore: DataStore<Preferences>,
            appApiService: AppApiService
        ): AppMainRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AppMainRepository(dataStore, appApiService)
                INSTANCE = instance
                instance
            }
        }
    }
}

