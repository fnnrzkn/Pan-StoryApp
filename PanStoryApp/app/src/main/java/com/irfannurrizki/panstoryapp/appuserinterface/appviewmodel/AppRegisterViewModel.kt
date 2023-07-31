package com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel

import androidx.lifecycle.ViewModel
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppMainRepository

class AppRegisterViewModel (private val pref: AppMainRepository) : ViewModel() {

    fun register(name: String, email: String, password: String) = pref.register(name, email, password)

}