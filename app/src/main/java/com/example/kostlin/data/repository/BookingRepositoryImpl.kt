package com.example.kostlin.data.repository

import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.core.network.executeRequest
import com.example.kostlin.core.network.map
import com.example.kostlin.data.mapper.toDomain
import com.example.kostlin.data.remote.dto.booking.BookingRequestDto
import com.example.kostlin.data.remote.service.ApiService
import com.example.kostlin.domain.model.Booking
import com.example.kostlin.domain.repository.BookingRepository

class BookingRepositoryImpl(
    private val apiService: ApiService
) : BookingRepository {

    override suspend fun getBookings(): ApiResult<List<Booking>> {
        return executeRequest(defaultValue = emptyList()) {
            apiService.getBookings()
        }.map { list -> list.map { it.toDomain() } }
    }

    override suspend fun createBooking(
        kosId: Int,
        bookingType: String,
        roomQuantity: Int,
        totalPrice: Int,
        note: String?
    ): ApiResult<Booking> {
        return executeRequest {
            apiService.createBooking(
                BookingRequestDto(
                    kosId = kosId,
                    bookingType = bookingType,
                    roomQuantity = roomQuantity,
                    totalPrice = totalPrice,
                    note = note
                )
            )
        }.map { it.toDomain() }
    }
}
