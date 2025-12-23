package com.example.kostlin.data.remote.dto.review

import com.squareup.moshi.Json

data class ReviewDto(
    @Json(name = "id") val id: Int,
    @Json(name = "userId") val userId: Int,
    @Json(name = "kosId") val kosId: Int,
    @Json(name = "rating") val rating: Double,
    @Json(name = "comment") val comment: String?,
    @Json(name = "createdAt") val createdAt: String,
    @Json(name = "user") val user: ReviewUserDto?
)

data class ReviewUserDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String?
)

data class ReviewRequestDto(
    @Json(name = "rating") val rating: Double,
    @Json(name = "comment") val comment: String? = null
)


