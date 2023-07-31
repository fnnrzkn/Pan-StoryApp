package com.irfannurrizki.panstoryapp.appdata.localdata

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppUserSession private constructor(private val dataStore: DataStore<Preferences>) {
    private val key = stringPreferencesKey("user_token")

    fun getUserToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: ""
        }
    }

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[key] = token
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppUserSession? = null

        fun getInstance(dataStore: DataStore<Preferences>): AppUserSession {
            return INSTANCE ?: synchronized(this) {
                val instance = AppUserSession(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}