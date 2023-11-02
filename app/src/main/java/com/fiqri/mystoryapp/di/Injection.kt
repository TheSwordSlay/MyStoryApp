package com.fiqri.mystoryapp.di

import android.content.Context
import com.fiqri.mystoryapp.data.remote.retrofit.ApiConfig
import com.fiqri.mystoryapp.helper.login.LoginPreferences
import com.fiqri.mystoryapp.helper.login.dataStore
import com.fiqri.mystoryapp.data.local.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = LoginPreferences.getInstance(context.dataStore)
        val user = runBlocking { pref.getLoginInfo("token").first() }
        val apiService = ApiConfig.getApiService(user)
        return StoryRepository(apiService)
    }
}