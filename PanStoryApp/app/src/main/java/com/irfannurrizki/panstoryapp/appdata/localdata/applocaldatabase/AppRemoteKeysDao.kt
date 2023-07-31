package com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<AppRemoteKeys>)

    @Query("SELECT * FROM remote_keys_data WHERE id = :id")
    suspend fun getRemoteKeysId(id: String?): AppRemoteKeys?

    @Query("DELETE FROM remote_keys_data")
    suspend fun deleteRemoteKeys()
}