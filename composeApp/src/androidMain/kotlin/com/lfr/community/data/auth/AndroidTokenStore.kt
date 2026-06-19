package com.lfr.community.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AndroidTokenStore(private val context: Context) : TokenStore {
    private val tokenKey = stringPreferencesKey("jwt_token")

    override suspend fun load(): String? {
        return context.dataStore.data.map { it[tokenKey] }.first()
    }

    override suspend fun save(token: String) {
        context.dataStore.edit { it[tokenKey] = token }
    }

    override suspend fun clear() {
        context.dataStore.edit { it.remove(tokenKey) }
    }
}
