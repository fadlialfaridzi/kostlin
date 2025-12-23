package com.example.kostlin.data.remote.dto.kos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateKosRequestDto(
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?,
    @Json(name = "address") val address: String,
    @Json(name = "city") val city: String,
    @Json(name = "latitude") val latitude: Double?,
    @Json(name = "longitude") val longitude: Double?,
    @Json(name = "pricePerMonth") val pricePerMonth: Int,
    @Json(name = "type") val type: String, // "putra", "putri", "campur"
    @Json(name = "thumbnailUrl") val thumbnailUrl: String? = null,
    @Json(name = "facilities") val facilities: List<FacilityDto>? = null
)

@JsonClass(generateAdapter = true)
data class FacilityDto(
    @Json(name = "name") val name: String,
    @Json(name = "icon") val icon: String? = null
)
