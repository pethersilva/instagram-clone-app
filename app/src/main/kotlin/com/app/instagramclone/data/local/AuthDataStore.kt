package com.app.instagramclone.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
    }

    val jwtToken: Flow<String?> = dataStore.data.map { it[JWT_TOKEN] }

    suspend fun saveToken(token: String) {
        dataStore.edit { it[JWT_TOKEN] = token }
    }

    suspend fun saveUserId(userId: String) {
        dataStore.edit { it[CURRENT_USER_ID] = userId }
    }

    suspend fun clearToken() {
        dataStore.edit {
            it.remove(JWT_TOKEN)
            it.remove(CURRENT_USER_ID)
        }
    }
}
