package com.app.instagramclone.data.remote.api

import com.app.instagramclone.data.remote.model.LoginRequest
import com.app.instagramclone.data.remote.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/token")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
