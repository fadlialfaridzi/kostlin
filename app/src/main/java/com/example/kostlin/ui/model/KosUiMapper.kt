package com.example.kostlin.ui.model

import com.example.kostlin.data.model.KosProperty
import com.example.kostlin.data.model.KosType
import com.example.kostlin.domain.model.Kos
import com.example.kostlin.domain.model.KosCategory

fun Kos.toKosProperty(): KosProperty = KosProperty(
    id = id.toIntOrNull() ?: id.hashCode(),
    name = name,
    location = address.ifBlank { city },
    price = formatPrice(pricePerMonth),
    pricePerMonth = pricePerMonth,
    rating = String.format("%.1f", rating),
    ratingValue = rating.toFloat(),
    type = type.toKosType(),
    facilities = facilities.map { it.name },
    description = description,
    imageUrl = images.firstOrNull() ?: thumbnailUrl,
    isPopular = isPopular,
    isRecommended = isRecommended,
    ownerId = owner.id.toIntOrNull()
)

fun List<Kos>.toKosPropertyList(): List<KosProperty> = map { it.toKosProperty() }

private fun KosCategory.toKosType(): KosType = when (this) {
    KosCategory.PUTRA -> KosType.PUTRA
    KosCategory.PUTRI -> KosType.PUTRI
    KosCategory.CAMPUR -> KosType.CAMPUR
}

private fun formatPrice(price: Int): String {
    val formatted = String.format("%,d", price).replace(',', '.')
    return "Rp $formatted/Bulan"
}
