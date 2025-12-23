package com.example.kostlin.domain.model

/**
 * Domain model for Booking
 */
data class Booking(
    val id: Int,
    val userId: Int,
    val kosId: Int,
    val bookingType: String,
    val roomQuantity: Int,
    val totalPrice: Int,
    val status: String,
    val note: String?,
    val createdAt: String,
    val kos: BookingKosSummary?,
    val user: BookingUser? = null
)

/**
 * User info for booking (booker)
 */
data class BookingUser(
    val id: Int?,
    val name: String?,
    val email: String?,
    val phone: String?
)

/**
 * Summary of Kos included in booking response
 */
data class BookingKosSummary(
    val id: Int,
    val name: String,
    val description: String?,
    val address: String,
    val city: String,
    val pricePerMonth: Int,
    val rating: Double,
    val type: String,
    val thumbnailUrl: String?,
    val facilities: List<String>,
    val ownerName: String? = null,
    val ownerPhone: String? = null
)
