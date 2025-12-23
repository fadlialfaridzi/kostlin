package com.example.kostlin.data.remote.dto.booking

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookingDto(
    @Json(name = "id") val id: Int,
    @Json(name = "userId") val userId: Int,
    @Json(name = "kosId") val kosId: Int,
    @Json(name = "bookingType") val bookingType: String,
    @Json(name = "roomQuantity") val roomQuantity: Int,
    @Json(name = "totalPrice") val totalPrice: Int,
    @Json(name = "status") val status: String,
    @Json(name = "note") val note: String?,
    @Json(name = "createdAt") val createdAt: String,
    @Json(name = "kos") val kos: KosSummaryDto?,
    @Json(name = "user") val user: UserSummaryDto? = null
)

@JsonClass(generateAdapter = true)
data class UserSummaryDto(
    @Json(name = "id") val id: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "phone") val phone: String?
)

@JsonClass(generateAdapter = true)
data class KosSummaryDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?,
    @Json(name = "address") val address: String,
    @Json(name = "city") val city: String,
    @Json(name = "pricePerMonth") val pricePerMonth: Int,
    @Json(name = "rating") val rating: Double,
    @Json(name = "type") val type: String,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String?,
    @Json(name = "facilities") val facilities: List<KosFacilitySummaryDto>?
)

@JsonClass(generateAdapter = true)
data class KosFacilitySummaryDto(
    @Json(name = "name") val name: String,
    @Json(name = "icon") val icon: String?
)

@JsonClass(generateAdapter = true)
data class BookingRequestDto(
    @Json(name = "kosId") val kosId: Int,
    @Json(name = "bookingType") val bookingType: String,
    @Json(name = "roomQuantity") val roomQuantity: Int,
    @Json(name = "totalPrice") val totalPrice: Int,
    @Json(name = "note") val note: String? = null
)
