package com.example.kostlin.domain.model

data class Kos(
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val city: String,
    val latitude: Double?,
    val longitude: Double?,
    val pricePerMonth: Int,
    val rating: Double,
    val type: KosCategory,
    val thumbnailUrl: String?,
    val facilities: List<KosFacility>,
    val owner: KosOwnerSummary,
    val images: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isPopular: Boolean = false,
    val isRecommended: Boolean = false,
    val isActive: Boolean? = true
)

data class KosOwnerSummary(
    val id: String,
    val name: String,
    val phoneNumber: String?,
    val email: String?
)

data class KosFacility(
    val name: String,
    val icon: String,
    val isAvailable: Boolean = true
)

data class KosReview(
    val id: String,
    val userName: String,
    val rating: Double,
    val comment: String,
    val createdAt: String
)

enum class KosCategory {
    PUTRA,
    PUTRI,
    CAMPUR
}
