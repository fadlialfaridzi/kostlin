package com.example.kostlin.ui.screen.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BookingConfirmationScreen(
    bookingDetail: BookingDetail,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val formattedTotalPrice = priceFormatter.format(bookingDetail.totalPrice).replace(",00", "")
    val formattedPricePerMonth = priceFormatter.format(bookingDetail.pricePerMonth).replace(",00", "")

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1B2633)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Konfirmasi Booking",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2633)
                )
            )
        }
        
        Divider(color = Color(0xFFE0E0E0))
        
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Kos Image & Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    // Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        if (bookingDetail.imageUrl != null) {
                            AsyncImage(
                                model = bookingDetail.imageUrl,
                                contentDescription = bookingDetail.kosName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF5876FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🏠",
                                    style = MaterialTheme.typography.displayMedium
                                )
                            }
                        }
                    }
                    
                    // Info
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = bookingDetail.kosName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B2633)
                            )
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = bookingDetail.kosAddress,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF6B7280)
                                )
                            )
                        }
                        
                        // Type Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF0F4FF)
                        ) {
                            Text(
                                text = "Kos ${bookingDetail.kosType}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF5876FF)
                                )
                            )
                        }
                    }
                }
            }
            
            // Facilities
            if (bookingDetail.kosFacilities.isNotEmpty()) {
                Column {
                    Text(
                        text = "Fasilitas",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1B2633)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bookingDetail.kosFacilities) { facility ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF5F5F5)
                            ) {
                                Text(
                                    text = facility,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF6B7280)
                                    )
                                )
                            }
                        }
                    }
                }
            }
            
            // Booking Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Detail Booking",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2633)
                        )
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tipe Booking",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280))
                        )
                        Text(
                            text = bookingDetail.bookingType.label,
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
                            text = "Jumlah Kamar",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280))
                        )
                        Text(
                            text = "${bookingDetail.roomQuantity} kamar",
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
                            text = "Harga per bulan",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280))
                        )
                        Text(
                            text = formattedPricePerMonth,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF1B2633))
                        )
                    }
                    
                    Divider(color = Color(0xFFE0E0E0))
                    
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
                            text = formattedTotalPrice,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5876FF)
                            )
                        )
                    }
                }
            }
        }
        
        // Confirm Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5876FF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Konfirmasi Booking",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}
