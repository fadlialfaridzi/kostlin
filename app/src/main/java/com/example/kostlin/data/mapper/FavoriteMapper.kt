package com.example.kostlin.data.mapper

import com.example.kostlin.data.remote.dto.favorite.FavoriteDto
import com.example.kostlin.domain.model.FavoriteEntry

fun FavoriteDto.toDomainFavorite(): FavoriteEntry = FavoriteEntry(
    id = id,
    userId = userId,
    kos = kos.toDomain(),
    status = when (status.lowercase()) {
        "active" -> com.example.kostlin.domain.model.FavoriteStatus.ACTIVE
        "removed" -> com.example.kostlin.domain.model.FavoriteStatus.REMOVED
        else -> com.example.kostlin.domain.model.FavoriteStatus.ACTIVE
    },
    dateAdded = dateAdded,
    removedAt = removedAt
)
