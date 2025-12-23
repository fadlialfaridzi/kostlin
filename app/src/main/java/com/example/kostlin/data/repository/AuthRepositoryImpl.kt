package com.example.kostlin.data.repository

import android.util.Log
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.core.network.executeRequest
import com.example.kostlin.core.network.map
import com.example.kostlin.data.local.session.AuthTokenStore
import com.example.kostlin.data.mapper.toDomain
import com.example.kostlin.data.mapper.toDomainUser
import com.example.kostlin.data.remote.dto.auth.LoginRequestDto
import com.example.kostlin.data.remote.dto.auth.RegisterRequestDto
import com.example.kostlin.data.remote.dto.auth.RequestResetPasswordDto
import com.example.kostlin.data.remote.dto.auth.ResetPasswordDto
import com.example.kostlin.data.remote.dto.auth.VerifyOtpDto
import com.example.kostlin.data.remote.service.ApiService
import com.example.kostlin.domain.model.User
import com.example.kostlin.domain.repository.AuthRepository
import com.example.kostlin.service.FcmTokenManager

class AuthRepositoryImpl(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun login(email: String, password: String): ApiResult<User> {
        val request = LoginRequestDto(email = email, password = password)
        val result = executeRequest { apiService.login(request) }
            .map { response ->
                AuthTokenStore.setTokens(
                    response.tokens.accessToken,
                    response.tokens.refreshToken
                )
                response.toDomainUser()
            }
        
        // Send FCM token after successful login (outside of map block)
        if (result is ApiResult.Success) {
            Log.d("AuthRepository", "Login successful, now sending FCM token...")
            sendFcmTokenToBackend()
        }
        
        return result
    }

    override suspend fun register(fullName: String, email: String, password: String, phoneNumber: String): ApiResult<User> {
        val request = RegisterRequestDto(fullName = fullName, email = email, password = password, phoneNumber = phoneNumber)
        val result = executeRequest { apiService.register(request) }
            .map { response ->
                AuthTokenStore.setTokens(
                    response.tokens.accessToken,
                    response.tokens.refreshToken
                )
                response.toDomainUser()
            }
        
        // Send FCM token after successful register (outside of map block)
        if (result is ApiResult.Success) {
            Log.d("AuthRepository", "Register successful, now sending FCM token...")
            sendFcmTokenToBackend()
        }
        
        return result
    }

    override suspend fun requestPasswordReset(email: String): ApiResult<Unit> {
        return executeRequest(defaultValue = Unit) {
            apiService.requestResetPassword(RequestResetPasswordDto(email = email))
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): ApiResult<Unit> {
        return executeRequest(defaultValue = Unit) {
            apiService.verifyOtp(VerifyOtpDto(email = email, otp = otp))
        }
    }

    override suspend fun resetPassword(email: String, otp: String, newPassword: String): ApiResult<Unit> {
        return executeRequest(defaultValue = Unit) {
            apiService.resetPassword(ResetPasswordDto(email = email, otp = otp, newPassword = newPassword))
        }
    }

    override suspend fun getProfile(): ApiResult<User> {
        return executeRequest { apiService.getProfile() }
            .map { dto -> dto.toDomain() }
    }

    override suspend fun logout(): ApiResult<Unit> {
        AuthTokenStore.clear()
        return ApiResult.success(Unit)
    }
    
    private suspend fun sendFcmTokenToBackend() {
        try {
            val fcmToken = FcmTokenManager.getToken()
            if (fcmToken != null) {
                val result = executeRequest(defaultValue = Unit) {
                    apiService.updateFcmToken(mapOf("token" to fcmToken))
                }
                when (result) {
                    is ApiResult.Success -> Log.d("AuthRepository", "FCM token sent successfully")
                    is ApiResult.Error -> Log.e("AuthRepository", "Failed to send FCM token: ${result.message}")
                }
            } else {
                Log.w("AuthRepository", "FCM token is null, skipping send")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error sending FCM token", e)
        }
    }
}

