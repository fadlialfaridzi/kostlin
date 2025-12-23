package com.example.kostlin.domain.repository

import android.graphics.Bitmap
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.domain.model.Kos

interface KosRepository {
    suspend fun getPopularKos(): ApiResult<List<Kos>>
    suspend fun getKosList(
        type: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        search: String? = null
    ): ApiResult<List<Kos>>
    suspend fun getKosDetail(id: String): ApiResult<Kos>
    suspend fun getRecommendations(currentKosId: String? = null): ApiResult<List<Kos>>
    suspend fun createKos(
        name: String,
        description: String?,
        address: String,
        city: String,
        latitude: Double?,
        longitude: Double?,
        pricePerMonth: Int,
        type: String,
        facilities: List<Pair<String, String?>>
    ): ApiResult<Kos>
    suspend fun uploadKosImages(kosId: Int, images: List<Bitmap>): ApiResult<List<String>>
}

