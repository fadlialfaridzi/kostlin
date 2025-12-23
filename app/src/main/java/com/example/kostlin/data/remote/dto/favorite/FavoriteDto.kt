package com.example.kostlin.data.remote.dto.favorite

import com.example.kostlin.data.mapper.toDomain
import com.example.kostlin.domain.model.FavoriteEntry
import com.example.kostlin.domain.model.FavoriteStatus
import com.squareup.moshi.Json

data class FavoriteDto(
    @Json(name = "id") val id: Int,
    @Json(name = "userId") val userId: Int,
    @Json(name = "status") val status: String,
    @Json(name = "dateAdded") val dateAdded: String,
    @Json(name = "removedAt") val removedAt: String?,
    @Json(name = "kos") val kos: com.example.kostlin.data.remote.dto.kos.KosDto
)

fun FavoriteDto.toDomain(): FavoriteEntry = FavoriteEntry(
    id = id,
    userId = userId,
    kos = kos.toDomain(),
    status = mapStatus(status),
    dateAdded = dateAdded,
    removedAt = removedAt
)

private fun mapStatus(status: String): FavoriteStatus = when (status.lowercase()) {
    "active" -> FavoriteStatus.ACTIVE
    "removed" -> FavoriteStatus.REMOVED
    else -> FavoriteStatus.ACTIVE
}
