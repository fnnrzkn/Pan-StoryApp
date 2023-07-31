package com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel

import androidx.lifecycle.ViewModel
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppMainRepository
import com.irfannurrizki.panstoryapp.appdata.localdata.AppUserSession

class AppMapViewModel(private val pref: AppMainRepository) : ViewModel() {
    fun getLocationStoriesMap(token: String) = pref.getLocationStoriesMap(token)
}