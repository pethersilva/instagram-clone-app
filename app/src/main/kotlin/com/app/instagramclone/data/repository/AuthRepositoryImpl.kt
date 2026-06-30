package com.app.instagramclone.data.repository

import com.app.instagramclone.data.local.AuthDataStore
import com.app.instagramclone.data.remote.api.AuthApiService
import com.app.instagramclone.data.remote.model.LoginRequest
import com.app.instagramclone.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val authDataStore: AuthDataStore
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Unit> = runCatching {
        val response = api.login(LoginRequest(username, password))
        authDataStore.saveToken(response.token)
        authDataStore.saveUserId(response.userId)
    }

    override suspend fun logout() {
        authDataStore.clearToken()
    }
}
