package com.example.kostlin.data.remote.dto.kos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KosImageDto(
    @Json(name = "id") val id: Int,
    @Json(name = "kosId") val kosId: Int? = null,
    @Json(name = "imageUrl") val imageUrl: String,
    @Json(name = "isPrimary") val isPrimary: Boolean? = null,
    @Json(name = "createdAt") val createdAt: String? = null
)
