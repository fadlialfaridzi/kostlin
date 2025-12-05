package com.example.kostlin.di

import android.content.Context
import com.example.kostlin.data.api.ApiService
import com.example.kostlin.data.api.RetrofitClient
import com.example.kostlin.data.local.TokenManager
import com.example.kostlin.data.repository.AuthRepository
import com.example.kostlin.data.repository.FavoriteRepository
import com.example.kostlin.data.repository.KosRepository

object AppModule {
    private var tokenManager: TokenManager? = null
    private val apiService: ApiService = RetrofitClient.apiService
    
    fun initialize(context: Context) {
        tokenManager = TokenManager(context)
    }
    
    fun getTokenManager(context: Context): TokenManager {
        if (tokenManager == null) {
            tokenManager = TokenManager(context)
        }
        return tokenManager!!
    }
    
    fun getAuthRepository(context: Context): AuthRepository {
        return AuthRepository(apiService, getTokenManager(context))
    }
    
    fun getKosRepository(): KosRepository {
        return KosRepository(apiService)
    }
    
    fun getFavoriteRepository(context: Context): FavoriteRepository {
        return FavoriteRepository(apiService, getTokenManager(context))
    }
}

