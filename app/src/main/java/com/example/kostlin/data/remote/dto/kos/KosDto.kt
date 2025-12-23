package com.example.kostlin.data.remote.dto.kos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KosDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "address") val address: String,
    @Json(name = "city") val city: String,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "pricePerMonth") val pricePerMonth: Int,
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "type") val type: String,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String? = null,
    @Json(name = "facilities") val facilities: List<KosFacilityDto>? = null,
    @Json(name = "owner") val owner: KosOwnerDto? = null,
    @Json(name = "images") val images: List<KosImageItemDto>? = null,
    @Json(name = "isFavorite") val isFavorite: Boolean? = null,
    @Json(name = "isPopular") val isPopular: Boolean? = null,
    @Json(name = "isRecommended") val isRecommended: Boolean? = null,
    @Json(name = "isActive") val isActive: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class KosImageItemDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "imageUrl") val imageUrl: String,
    @Json(name = "isPrimary") val isPrimary: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class KosFacilityDto(
    @Json(name = "name") val name: String,
    @Json(name = "icon") val icon: String? = null,
    @Json(name = "isAvailable") val isAvailable: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class KosOwnerDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "phoneNumber") val phoneNumber: String? = null,
    @Json(name = "email") val email: String? = null
)
