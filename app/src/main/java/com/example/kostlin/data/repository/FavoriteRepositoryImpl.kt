package com.example.kostlin.data.repository

import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.core.network.executeRequest
import com.example.kostlin.core.network.map
import com.example.kostlin.data.mapper.toDomainFavorite
import com.example.kostlin.data.remote.service.ApiService
import com.example.kostlin.domain.model.FavoriteEntry
import com.example.kostlin.domain.repository.FavoriteRepository

class FavoriteRepositoryImpl(
    private val apiService: ApiService
) : FavoriteRepository {

    override suspend fun getFavorites(includeHistory: Boolean): ApiResult<List<FavoriteEntry>> {
        return executeRequest(defaultValue = emptyList()) {
            apiService.listFavorites(includeHistory)
        }.map { list -> list.map { it.toDomainFavorite() } }
    }

    override suspend fun addFavorite(kosId: Int): ApiResult<FavoriteEntry> {
        return executeRequest { apiService.addFavorite(mapOf("kosId" to kosId)) }
            .map { it.toDomainFavorite() }
    }

    override suspend fun removeFavorite(kosId: Int): ApiResult<Unit> {
        return executeRequest { apiService.removeFavorite(kosId) }.map { Unit }
    }
}
