package com.example.kostlin.data.mapper

import com.example.kostlin.data.remote.dto.booking.BookingDto
import com.example.kostlin.domain.model.Booking
import com.example.kostlin.domain.model.BookingKosSummary
import com.example.kostlin.domain.model.BookingUser

fun BookingDto.toDomain(): Booking {
    return Booking(
        id = id,
        userId = userId,
        kosId = kosId,
        bookingType = bookingType,
        roomQuantity = roomQuantity,
        totalPrice = totalPrice,
        status = status,
        note = note,
        createdAt = createdAt,
        kos = kos?.let { kosSummary ->
            BookingKosSummary(
                id = kosSummary.id,
                name = kosSummary.name,
                description = kosSummary.description,
                address = kosSummary.address,
                city = kosSummary.city,
                pricePerMonth = kosSummary.pricePerMonth,
                rating = kosSummary.rating,
                type = kosSummary.type,
                thumbnailUrl = kosSummary.thumbnailUrl,
                facilities = kosSummary.facilities?.map { it.name } ?: emptyList()
            )
        },
        user = user?.let { userDto ->
            BookingUser(
                id = userDto.id,
                name = userDto.name,
                email = userDto.email,
                phone = userDto.phone
            )
        }
    )
}
