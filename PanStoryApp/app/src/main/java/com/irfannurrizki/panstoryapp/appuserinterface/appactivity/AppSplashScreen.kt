package com.irfannurrizki.panstoryapp.appuserinterface.appactivity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.irfannurrizki.panstoryapp.R

class AppSplashScreen : AppCompatActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_splash_screen)

        supportActionBar?.hide()

        var handler= Handler()
        handler.postDelayed({
//            val intent = Intent (this, MainActivity::class.java)
            startActivity(Intent(this,AppLoginActivity::class.java))
            finish()
        },5000)
    }
}