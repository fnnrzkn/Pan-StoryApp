package com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppMainRepository
import com.irfannurrizki.panstoryapp.appdata.localdata.AppUserSession
import com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase.AppUserModel
import kotlinx.coroutines.launch

class AppLoginViewModel (private val pref: AppMainRepository) : ViewModel() {
    fun login(email: String, password: String) = pref.login(email, password)

    fun saveUser(user: AppUserModel){
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun getUser() = pref.getUser()

    fun saveToken(token: String){
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun isLogin() : LiveData<Boolean>{
        return pref.isLogin().asLiveData()
    }

    fun login() {
        viewModelScope.launch {
            pref.login()
        }
    }

    fun logout(){
        viewModelScope.launch {
            pref.logout()
        }
    }
}

