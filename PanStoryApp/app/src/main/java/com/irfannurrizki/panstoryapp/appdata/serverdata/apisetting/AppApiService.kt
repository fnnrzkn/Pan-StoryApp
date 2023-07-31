package com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting

import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface AppApiService {
    @POST("register")
    @FormUrlEncoded
    suspend fun addUser(@Field("name") name: String,
                        @Field("email")email: String,
                        @Field("password")password: String): RegisterResponse

    @POST("login")
    @FormUrlEncoded
    suspend fun getUser(@Field("email")email: String,
                        @Field("password")password: String): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(@Part("description")description: RequestBody,
                         @Part file: MultipartBody.Part,
                         @Header("Authorization")token: String,
                         @Part("lat") latitude: RequestBody?,
                         @Part("lon") longitude: RequestBody?): AddNewStoryResponse

    @GET("stories")
    suspend fun getAllStory(
        @Header("Authorization")token: String,
        @Query("page")page: Int,
        @Query("size")size: Int): GetAllStoryResponse

    @GET("stories?location=1")
        suspend fun getStoriesMapsLocation(
        @Header("Authorization") token: String
    ): GetAllStoryResponse
}