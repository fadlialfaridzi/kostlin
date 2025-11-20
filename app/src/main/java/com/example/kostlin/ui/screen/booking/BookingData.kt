package com.example.kostlin.ui.screen.booking

import java.time.LocalDate

data class BookingRequest(
    val kosId: Int,
    val checkInDate: LocalDate? = null,
    val checkOutDate: LocalDate? = null,
    val capacity: Int = 1,
    val roomType: String = "3 x 4 Meter"
)

data class BookingDetail(
    val kosId: Int,
    val kosName: String,
    val location: String,
    val pricePerMonth: Int,
    val rating: Float,
    val imageUrl: String? = null,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val capacity: Int,
    val roomType: String,
    val ownerEmail: String,
    val ownerPhone: String,
    val userPhone: String
) {
    fun getTotalDays(): Int {
        return (checkOutDate.toEpochDay() - checkInDate.toEpochDay()).toInt()
    }

    fun getTotalPrice(): Int {
        return pricePerMonth * getTotalDays() / 30
    }
}
