package com.diet.android.data.repository

import com.diet.android.data.api.ApiService
import com.diet.android.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val apiService: ApiService) {

    suspend fun loginWithPassword(request: LoginRequest): Result<JwtAuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.loginWithPassword(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(token: String): Result<JwtAuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.loginWithGoogle(TokenRequest(token))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithFacebook(token: String): Result<JwtAuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.loginWithFacebook(TokenRequest(token))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.forgotPassword(email)
            Result.success(response.string())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun applyDietitian(dto: DietitianApplicationDto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.applyDietitian(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(request: ProfileUpdateRequest): Result<UserInfo> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateProfile(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<UserInfo> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCurrentUser()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
