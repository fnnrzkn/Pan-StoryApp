package com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys_data")
data class AppRemoteKeys(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)