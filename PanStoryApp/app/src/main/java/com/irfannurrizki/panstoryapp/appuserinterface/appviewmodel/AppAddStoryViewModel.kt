package com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppMainRepository
import com.irfannurrizki.panstoryapp.appdata.localdata.AppUserSession
import com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase.AppUserModel

class AppAddStoryViewModel (private val pref: AppMainRepository) : ViewModel() {
    fun getUserToken(): LiveData<AppUserModel> {
        return pref.getUser().asLiveData()
    }
}