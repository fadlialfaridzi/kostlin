package com.example.kostlin.domain.repository

import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.domain.model.Booking

interface BookingRepository {
    suspend fun getBookings(): ApiResult<List<Booking>>
    suspend fun createBooking(
        kosId: Int,
        bookingType: String,
        roomQuantity: Int,
        totalPrice: Int,
        note: String?
    ): ApiResult<Booking>
}
