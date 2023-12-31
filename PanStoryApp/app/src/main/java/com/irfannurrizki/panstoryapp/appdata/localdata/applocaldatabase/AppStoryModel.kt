package com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stories")
data class Story (
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("photoUrl")
    val photoUrl: String?,
    @field:SerializedName("createdAt")
    val createdAt: String?,
    @field:SerializedName("lat")
    val lat: Float? = null,
    @field:SerializedName("lon")
    val lon: Float? = null,
) : Parcelable
