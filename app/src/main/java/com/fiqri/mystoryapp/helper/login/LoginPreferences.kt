package com.fiqri.mystoryapp.helper.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

class LoginPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveLoginInfo(keyName: String, data: String) {
        val loginInfoKey = stringPreferencesKey(keyName)
        dataStore.edit { preferences ->
            preferences[loginInfoKey] = data
        }
    }

    fun getLoginInfo(keyName: String): Flow<String> {
        val loginInfoKey = stringPreferencesKey(keyName)
        return dataStore.data.map { preferences ->
            preferences[loginInfoKey] ?: ""
        }
    }

    suspend fun logout() {
        val loginUsername = stringPreferencesKey("username")
        val loginPassword = stringPreferencesKey("password")
        val loginToken = stringPreferencesKey("token")
        dataStore.edit { preferences ->
            preferences[loginUsername] = ""
            preferences[loginPassword] = ""
            preferences[loginToken] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): LoginPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}