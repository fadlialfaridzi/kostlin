package com.example.kostlin.core.di

import com.example.kostlin.core.network.NetworkClient
import com.example.kostlin.data.repository.AuthRepositoryImpl
import com.example.kostlin.data.repository.BookingRepositoryImpl
import com.example.kostlin.data.repository.FavoriteRepositoryImpl
import com.example.kostlin.data.repository.KosRepositoryImpl
import com.example.kostlin.domain.repository.AuthRepository
import com.example.kostlin.domain.repository.BookingRepository
import com.example.kostlin.domain.repository.FavoriteRepository
import com.example.kostlin.domain.repository.KosRepository

object AppContainer {
    private val apiService by lazy { NetworkClient.apiService }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(apiService)
    }

    val kosRepository: KosRepository by lazy {
        KosRepositoryImpl(apiService)
    }

    val favoriteRepository: FavoriteRepository by lazy {
        FavoriteRepositoryImpl(apiService)
    }

    val bookingRepository: BookingRepository by lazy {
        BookingRepositoryImpl(apiService)
    }
}
