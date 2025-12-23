package com.example.kostlin.ui.screen.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kostlin.data.model.KosProperty
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BookingRequestScreen(
    kosProperty: KosProperty,
    onBackClick: () -> Unit,
    onContinueClick: (BookingType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBookingType by remember { mutableStateOf(BookingType.MONTHLY) }
    var roomQuantity by remember { mutableIntStateOf(1) }
    var showDropdown by remember { mutableStateOf(false) }
    
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    // Calculate total price
    val totalPrice = kosProperty.pricePerMonth * selectedBookingType.multiplier * roomQuantity
    val formattedTotalPrice = priceFormatter.format(totalPrice).replace(",00", "")
    val formattedPricePerMonth = priceFormatter.format(kosProperty.pricePerMonth).replace(",00", "")

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
                text = "Pesan Kos",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2633)
                )
            )
        }
        
        Divider(color = Color(0xFFE0E0E0))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Kos Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F8FF))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = kosProperty.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2633)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = kosProperty.location,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF6B7280)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$formattedPricePerMonth / bulan",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5876FF)
                        )
                    )
                }
            }
            
            // Booking Type Selection
            Column {
                Text(
                    text = "Tipe Booking",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1B2633)
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDropdown = true },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedBookingType.label,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF1B2633)
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select",
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }
                    
                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false }
                    ) {
                        BookingType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        text = type.label,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    selectedBookingType = type
                                    showDropdown = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Room Quantity
            Column {
                Text(
                    text = "Jumlah Kamar",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1B2633)
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (roomQuantity > 1) roomQuantity-- },
                        enabled = roomQuantity > 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = if (roomQuantity > 1) Color(0xFF5876FF) else Color(0xFFBDBDBD)
                        )
                    }
                    
                    Text(
                        text = "$roomQuantity Kamar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2633)
                        )
                    )
                    
                    IconButton(
                        onClick = { if (roomQuantity < 10) roomQuantity++ },
                        enabled = roomQuantity < 10
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = if (roomQuantity < 10) Color(0xFF5876FF) else Color(0xFFBDBDBD)
                        )
                    }
                }
            }
            
            // Price Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ringkasan Harga",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1B2633)
                        )
                    )
                    
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
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Durasi",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280))
                        )
                        Text(
                            text = if (selectedBookingType == BookingType.YEARLY) "12 bulan" else "1 bulan",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF1B2633))
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Jumlah kamar",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280))
                        )
                        Text(
                            text = "$roomQuantity kamar",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF1B2633))
                        )
                    }
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color(0xFFE0E0E0)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
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
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Continue Button
        Button(
            onClick = { onContinueClick(selectedBookingType, roomQuantity) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5876FF)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Lanjutkan Booking",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}
