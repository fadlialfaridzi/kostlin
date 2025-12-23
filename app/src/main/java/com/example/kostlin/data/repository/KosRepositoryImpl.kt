package com.example.kostlin.data.repository

import android.graphics.Bitmap
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.core.network.executeRequest
import com.example.kostlin.core.network.map
import com.example.kostlin.data.mapper.toDomain
import com.example.kostlin.data.mapper.toDomainList
import com.example.kostlin.data.remote.dto.kos.CreateKosRequestDto
import com.example.kostlin.data.remote.dto.kos.FacilityDto
import com.example.kostlin.data.remote.service.ApiService
import com.example.kostlin.domain.model.Kos
import com.example.kostlin.domain.repository.KosRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class KosRepositoryImpl(
    private val apiService: ApiService
) : KosRepository {

    override suspend fun getPopularKos(): ApiResult<List<Kos>> {
        return executeRequest(defaultValue = emptyList()) { apiService.getPopularKos() }
            .map { it.toDomainList() }
    }

    override suspend fun getKosList(
        type: String?,
        minPrice: Int?,
        maxPrice: Int?,
        search: String?
    ): ApiResult<List<Kos>> {
        return executeRequest(defaultValue = emptyList()) {
            apiService.getKosList(type = type, minPrice = minPrice, maxPrice = maxPrice, search = search)
        }.map { it.toDomainList() }
    }

    override suspend fun getKosDetail(id: String): ApiResult<Kos> {
        return executeRequest { apiService.getKosDetail(id) }
            .map { it.toDomain() }
    }

    override suspend fun getRecommendations(currentKosId: String?): ApiResult<List<Kos>> {
        return executeRequest(defaultValue = emptyList()) {
            apiService.getRecommendedKos()
        }.map { it.toDomainList() }
    }

    override suspend fun createKos(
        name: String,
        description: String?,
        address: String,
        city: String,
        latitude: Double?,
        longitude: Double?,
        pricePerMonth: Int,
        type: String,
        facilities: List<Pair<String, String?>>
    ): ApiResult<Kos> {
        val request = CreateKosRequestDto(
            name = name,
            description = description,
            address = address,
            city = city,
            latitude = latitude,
            longitude = longitude,
            pricePerMonth = pricePerMonth,
            type = type.lowercase(),
            facilities = facilities.map { FacilityDto(name = it.first, icon = it.second) }
        )
        return executeRequest { apiService.createKos(request) }
            .map { it.toDomain() }
    }

    override suspend fun uploadKosImages(kosId: Int, images: List<Bitmap>): ApiResult<List<String>> {
        val parts = images.mapIndexed { index, bitmap ->
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            val byteArray = stream.toByteArray()
            val requestBody = byteArray.toRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("images", "kos_${kosId}_$index.jpg", requestBody)
        }
        
        return executeRequest(defaultValue = emptyList()) { apiService.uploadKosImages(kosId, parts) }
            .map { list -> list.map { it.imageUrl } }
    }
}
