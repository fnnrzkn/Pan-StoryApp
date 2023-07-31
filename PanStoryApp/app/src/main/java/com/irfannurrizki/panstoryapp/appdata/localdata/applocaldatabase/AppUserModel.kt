package com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class AppUserModel (
    @SerializedName("userId")
    val userId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("token")
    val token: String,
    val isLogin: Boolean = false
) : Parcelable



