package com.example.kostlin.data.repository

import com.example.kostlin.data.api.ApiService
import com.example.kostlin.data.api.model.CreateKosRequest
import com.example.kostlin.data.api.model.KosPropertyDto

class KosRepository(private val apiService: ApiService) {
    
    suspend fun getAllKos(
        type: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        search: String? = null,
        popular: Boolean? = null,
        recommended: Boolean? = null
    ): Result<List<KosPropertyDto>> {
        return try {
            val response = apiService.getAllKos(type, minPrice, maxPrice, search, popular, recommended)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to fetch kos"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getKosById(id: Int): Result<KosPropertyDto> {
        return try {
            val response = apiService.getKosById(id)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Kos not found"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPopularKos(): Result<List<KosPropertyDto>> {
        return try {
            val response = apiService.getPopularKos()
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to fetch popular kos"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRecommendedKos(): Result<List<KosPropertyDto>> {
        return try {
            val response = apiService.getRecommendedKos()
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to fetch recommended kos"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchKos(query: String): Result<List<KosPropertyDto>> {
        return try {
            val response = apiService.searchKos(query)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Search failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createKos(
        name: String,
        location: String,
        pricePerMonth: Int,
        type: String,
        description: String? = null,
        ownerEmail: String? = null,
        ownerPhone: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        facilities: List<String> = emptyList(),
        imageUrl: String? = null
    ): Result<KosPropertyDto> {
        return try {
            val request = CreateKosRequest(
                name = name,
                location = location,
                pricePerMonth = pricePerMonth,
                type = type,
                description = description,
                ownerEmail = ownerEmail,
                ownerPhone = ownerPhone,
                latitude = latitude,
                longitude = longitude,
                facilities = facilities,
                imageUrl = imageUrl
            )
            
            val response = apiService.createKos(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to create kos"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

