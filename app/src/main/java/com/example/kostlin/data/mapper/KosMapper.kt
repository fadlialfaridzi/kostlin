package com.example.kostlin.data.mapper

import com.example.kostlin.data.remote.dto.kos.KosDto
import com.example.kostlin.data.remote.dto.kos.KosFacilityDto
import com.example.kostlin.data.remote.dto.kos.KosOwnerDto
import com.example.kostlin.domain.model.Kos
import com.example.kostlin.domain.model.KosCategory
import com.example.kostlin.domain.model.KosFacility
import com.example.kostlin.domain.model.KosOwnerSummary

fun KosDto.toDomain(): Kos = Kos(
    id = id.toString(),
    name = name,
    description = description ?: "",
    address = address,
    city = city,
    latitude = latitude,
    longitude = longitude,
    pricePerMonth = pricePerMonth,
    rating = rating ?: 0.0,
    type = type.toKosCategory(),
    thumbnailUrl = thumbnailUrl,
    facilities = facilities?.map(KosFacilityDto::toDomain) ?: emptyList(),
    owner = owner?.toDomain() ?: KosOwnerSummary("", "", null, null),
    images = images?.map { it.imageUrl } ?: emptyList(),
    isFavorite = isFavorite ?: false,
    isPopular = isPopular ?: false,
    isRecommended = isRecommended ?: false,
    isActive = isActive ?: true
)

fun List<KosDto>.toDomainList(): List<Kos> = map { it.toDomain() }

private fun String.toKosCategory(): KosCategory = when (lowercase()) {
    "putra" -> KosCategory.PUTRA
    "putri" -> KosCategory.PUTRI
    else -> KosCategory.CAMPUR
}

private fun KosFacilityDto.toDomain(): KosFacility = KosFacility(
    name = name,
    icon = icon ?: "",
    isAvailable = isAvailable ?: true
)

private fun KosOwnerDto.toDomain(): KosOwnerSummary = KosOwnerSummary(
    id = id?.toString() ?: "",
    name = name ?: "",
    phoneNumber = phoneNumber,
    email = email
)

