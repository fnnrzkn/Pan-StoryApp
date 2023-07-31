package com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppMainRepository
import com.irfannurrizki.panstoryapp.appdata.localdata.AppUserSession

class AppViewModelFactory(private val pref: AppMainRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AppRegisterViewModel::class.java) -> {
                AppRegisterViewModel(pref) as T
            }
            modelClass.isAssignableFrom(AppLoginViewModel::class.java) -> {
                AppLoginViewModel(pref) as T
            }

            modelClass.isAssignableFrom(AppMapViewModel::class.java) -> {
                AppMapViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown Viewmodel Class: " + modelClass.name)
        }
    }

}