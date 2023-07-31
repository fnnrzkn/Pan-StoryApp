package com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddNewStoryResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem>

):Parcelable
