package com.example.kostlin.data.repository

import com.example.kostlin.data.api.ApiService
import com.example.kostlin.data.api.model.FavoriteKosDto
import com.example.kostlin.data.api.model.FavoriteStatusResponse
import com.example.kostlin.data.local.TokenManager

class FavoriteRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    
    suspend fun addToFavorite(kosId: Int): Result<Unit> {
        return try {
            val token = tokenManager.getAuthHeader()
            val response = apiService.addToFavorite(token, kosId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to add to favorites"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeFromFavorite(kosId: Int): Result<Unit> {
        return try {
            val token = tokenManager.getAuthHeader()
            val response = apiService.removeFromFavorite(token, kosId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to remove from favorites"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserFavorites(): Result<List<FavoriteKosDto>> {
        return try {
            val token = tokenManager.getAuthHeader()
            val response = apiService.getUserFavorites(token)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to fetch favorites"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun checkFavoriteStatus(kosId: Int): Result<Boolean> {
        return try {
            val token = tokenManager.getAuthHeader()
            val response = apiService.checkFavoriteStatus(token, kosId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!.isFavorite)
            } else {
                Result.failure(Exception("Failed to check favorite status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

