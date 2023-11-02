package com.fiqri.mystoryapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.fiqri.mystoryapp.R
import com.fiqri.mystoryapp.helper.login.LoginPreferences
import com.fiqri.mystoryapp.helper.login.LoginViewModel
import com.fiqri.mystoryapp.helper.login.LoginViewModelFactory
import com.fiqri.mystoryapp.helper.login.dataStore

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var isLoggedIn = false
        supportActionBar?.hide()
        val pref = LoginPreferences.getInstance(application.dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(pref)).get(
            LoginViewModel::class.java
        )

        loginViewModel.getLoginInfo("password").observe(this) { password: String? ->
            if(password?.length!! >= 8) {
                isLoggedIn = true
            }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if(isLoggedIn) {
                val toStory = Intent(this, StoryListActivity::class.java)
                toStory.putExtra("reload", "no")
                startActivity(toStory)
                finish()
            } else {
                val toRegister = Intent(this, RegisterActivity::class.java)
                startActivity(toRegister)
                finish()
            }
        }, 1500)
    }
}