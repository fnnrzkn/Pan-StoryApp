package com.irfannurrizki.panstoryapp.appdata.apprepo

sealed class AppResult <out R> private constructor() {
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Error(val error: String) : AppResult<Nothing>()
    object Loading : AppResult<Nothing>()
}