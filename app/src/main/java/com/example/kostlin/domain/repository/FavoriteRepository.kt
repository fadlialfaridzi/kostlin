package com.example.kostlin.domain.repository

import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.domain.model.FavoriteEntry

interface FavoriteRepository {
    suspend fun getFavorites(includeHistory: Boolean = false): ApiResult<List<FavoriteEntry>>
    suspend fun addFavorite(kosId: Int): ApiResult<FavoriteEntry>
    suspend fun removeFavorite(kosId: Int): ApiResult<Unit>
}
