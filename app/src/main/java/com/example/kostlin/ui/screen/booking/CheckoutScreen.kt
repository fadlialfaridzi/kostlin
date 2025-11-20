package com.example.kostlin.ui.screen.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.format.DateTimeFormatter

@Composable
fun CheckoutScreen(
    bookingDetail: BookingDetail,
    onBackClick: () -> Unit,
    onConfirmBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1B2633)
                )
            }

            Text(
                text = "Pesanan",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2633)
                )
            )

            IconButton(onClick = { /* TODO: Menu */ }) {
                Text(text = "⋮", fontSize = 20.sp, color = Color(0xFF1B2633))
            }
        }

        Divider(color = Color(0xFFE0E0E0))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Property Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Property Image
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = bookingDetail.kosName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B2633)
                                )
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "📍", fontSize = 12.sp)
                                Text(
                                    text = bookingDetail.location,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF6B7280)
                                    )
                                )
                            }

                            Text(
                                text = "Rp ${String.format("%,d", bookingDetail.pricePerMonth).replace(",", ".")} /bulan",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF5876FF)
                                )
                            )
                        }

                        Text(
                            text = "⭐ ${bookingDetail.rating}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B2633)
                            )
                        )
                    }
                }
            }

            // Pesanan Kamu Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Pesanan Kamu",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5876FF)
                        )
                    )

                    BookingDetailRow(
                        icon = "📅",
                        label = "Tanggal",
                        value = "${bookingDetail.checkInDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))} - ${bookingDetail.checkOutDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}"
                    )

                    BookingDetailRow(
                        icon = "👤",
                        label = "Kapasitas",
                        value = "${bookingDetail.capacity} Orang"
                    )

                    BookingDetailRow(
                        icon = "📐",
                        label = "Tipe Kamar",
                        value = bookingDetail.roomType
                    )

                    BookingDetailRow(
                        icon = "📞",
                        label = "Telepon",
                        value = bookingDetail.userPhone
                    )
                }
            }

            // Divider
            item {
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            }

            // Kontak Pemilik Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Kontak Pemilik",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5876FF)
                        )
                    )

                    BookingDetailRow(
                        icon = "✉️",
                        label = "Email",
                        value = bookingDetail.ownerEmail
                    )

                    BookingDetailRow(
                        icon = "📞",
                        label = "Telepon",
                        value = bookingDetail.ownerPhone
                    )
                }
            }

            // Divider
            item {
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            }

            // Price Summary
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Harga per hari",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF6B7280)
                            )
                        )
                        Text(
                            text = "Rp ${String.format("%,d", bookingDetail.pricePerMonth / 30).replace(",", ".")}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1B2633)
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Jumlah hari (${bookingDetail.getTotalDays()} hari)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF6B7280)
                            )
                        )
                        Text(
                            text = "Rp ${String.format("%,d", bookingDetail.pricePerMonth / 30 * bookingDetail.getTotalDays()).replace(",", ".")}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1B2633)
                            )
                        )
                    }

                    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Pembayaran",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B2633)
                            )
                        )
                        Text(
                            text = "Rp ${String.format("%,d", bookingDetail.getTotalPrice()).replace(",", ".")}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B2633)
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Booking Button
        Button(
            onClick = onConfirmBooking,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5876FF)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Booking Sekarang",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun BookingDetailRow(
    icon: String,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = icon, fontSize = 16.sp)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF6B7280)
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1B2633)
                )
            )
        }
    }
}
