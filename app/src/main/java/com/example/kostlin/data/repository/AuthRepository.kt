package com.example.kostlin.data.repository

import com.example.kostlin.data.api.ApiService
import com.example.kostlin.data.api.model.*
import com.example.kostlin.data.local.TokenManager

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun register(fullName: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(fullName, email, password)
            val response = apiService.register(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()!!.data!!
                tokenManager.saveToken(authData.token)
                tokenManager.saveUserInfo(authData.userId, authData.fullName, authData.email)
                Result.success(authData)
            } else {
                val errorMessage = response.body()?.message ?: "Registration failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()!!.data!!
                tokenManager.saveToken(authData.token)
                tokenManager.saveUserInfo(authData.userId, authData.fullName, authData.email)
                Result.success(authData)
            } else {
                val errorMessage = response.body()?.message ?: "Login failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val request = ForgotPasswordRequest(email)
            val response = apiService.forgotPassword(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to send OTP"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verifyOTP(email: String, code: String): Result<Unit> {
        return try {
            val request = VerifyOTPRequest(email, code)
            val response = apiService.verifyOTP(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Invalid or expired OTP"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun resetPassword(email: String, password: String): Result<Unit> {
        return try {
            val request = ResetPasswordRequest(email, password)
            val response = apiService.resetPassword(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to reset password"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProfile(): Result<UserProfile> {
        return try {
            val token = tokenManager.getAuthHeader()
            val response = apiService.getProfile(token)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get profile"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logout() {
        tokenManager.clear()
    }
    
    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }
}

