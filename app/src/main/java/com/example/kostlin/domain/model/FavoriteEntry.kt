package com.example.kostlin.domain.model

data class FavoriteEntry(
    val id: Int,
    val userId: Int,
    val kos: Kos,
    val status: FavoriteStatus,
    val dateAdded: String,
    val removedAt: String?
)

enum class FavoriteStatus {
    ACTIVE,
    REMOVED
}
