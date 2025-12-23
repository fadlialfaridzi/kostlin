package com.example.kostlin.ui.screen.booking

import com.example.kostlin.data.model.KosProperty

/**
 * Tipe booking: Bulanan atau Tahunan
 */
enum class BookingType(val label: String, val multiplier: Int) {
    MONTHLY("Bulanan", 1),
    YEARLY("Tahunan", 12)
}

/**
 * Data untuk request booking
 */
data class BookingRequest(
    val kosId: Int,
    val bookingType: BookingType = BookingType.MONTHLY,
    val roomQuantity: Int = 1,
    val note: String? = null
) {
    fun calculateTotalPrice(pricePerMonth: Int): Int {
        return pricePerMonth * bookingType.multiplier * roomQuantity
    }
}

/**
 * Detail booking untuk konfirmasi
 */
data class BookingDetail(
    val kosId: Int,
    val kosName: String,
    val kosAddress: String,
    val kosType: String,
    val kosFacilities: List<String>,
    val pricePerMonth: Int,
    val imageUrl: String?,
    val bookingType: BookingType,
    val roomQuantity: Int,
    val totalPrice: Int
) {
    companion object {
        fun fromKosProperty(
            kosProperty: KosProperty,
            bookingType: BookingType,
            roomQuantity: Int
        ): BookingDetail {
            val totalPrice = kosProperty.pricePerMonth * bookingType.multiplier * roomQuantity
            return BookingDetail(
                kosId = kosProperty.id,
                kosName = kosProperty.name,
                kosAddress = kosProperty.location,
                kosType = kosProperty.type.name,
                kosFacilities = kosProperty.facilities,
                pricePerMonth = kosProperty.pricePerMonth,
                imageUrl = kosProperty.imageUrl,
                bookingType = bookingType,
                roomQuantity = roomQuantity,
                totalPrice = totalPrice
            )
        }
    }
}
