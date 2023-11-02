package com.fiqri.mystoryapp.helper.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: LoginPreferences) : ViewModel() {
    fun getLoginInfo(key: String): LiveData<String> {
        return pref.getLoginInfo(key).asLiveData()
    }

    fun saveLoginInfo(key: String, data: String) {
        viewModelScope.launch {
            pref.saveLoginInfo(key, data)
        }
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}