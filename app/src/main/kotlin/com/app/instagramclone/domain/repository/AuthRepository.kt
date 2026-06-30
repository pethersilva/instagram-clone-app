package com.app.instagramclone.domain.repository

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun logout()
}
