package com.irfannurrizki.panstoryapp.appuserinterface.appcustomview

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.irfannurrizki.panstoryapp.R

class CustomLoadDialog (context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.app_load_custom)
    }
}