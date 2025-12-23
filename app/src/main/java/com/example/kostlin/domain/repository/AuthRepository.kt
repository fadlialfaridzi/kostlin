package com.example.kostlin.domain.repository

import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): ApiResult<User>
    suspend fun register(fullName: String, email: String, password: String, phoneNumber: String): ApiResult<User>
    suspend fun requestPasswordReset(email: String): ApiResult<Unit>
    suspend fun verifyOtp(email: String, otp: String): ApiResult<Unit>
    suspend fun resetPassword(email: String, otp: String, newPassword: String): ApiResult<Unit>
    suspend fun getProfile(): ApiResult<User>
    suspend fun logout(): ApiResult<Unit>
}
