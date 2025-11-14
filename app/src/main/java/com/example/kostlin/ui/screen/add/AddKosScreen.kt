package com.example.kostlin.ui.screen.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kostlin.ui.components.BottomNavigation
import com.example.kostlin.ui.components.BottomNavRoute

data class KosFacilityOption(
    val name: String,
    val icon: String,
    var isSelected: Boolean = false
)

@Composable
fun AddKosScreen(
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var kosName by remember { mutableStateOf("") }
    var kosAddress by remember { mutableStateOf("") }
    var ownerEmail by remember { mutableStateOf("") }
    var ownerWhatsapp by remember { mutableStateOf("") }
    var ownerInstagram by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    
    val facilityOptions = remember {
        mutableListOf(
            KosFacilityOption("WiFi", "📶"),
            KosFacilityOption("AC", "❄️"),
            KosFacilityOption("Kamar Mandi Dalam", "🚿"),
            KosFacilityOption("Dapur Bersama", "🍳"),
            KosFacilityOption("Parkir Motor", "🏍️"),
            KosFacilityOption("Security 24 Jam", "🛡️"),
            KosFacilityOption("Laundry", "👕"),
            KosFacilityOption("Ruang Belajar", "📚"),
            KosFacilityOption("Gym", "💪"),
            KosFacilityOption("Rooftop", "🏠")
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation(
                currentRoute = BottomNavRoute.ADD.route,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            // Header
            AddKosHeader(onBackClick = onBackClick)
            
            // Form Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nama Kost
                item {
                    FormField(
                        label = "Nama Kost",
                        value = kosName,
                        onValueChange = { kosName = it },
                        placeholder = "Masukkan nama kost"
                    )
                }
                
                // Alamat
                item {
                    FormField(
                        label = "Alamat",
                        value = kosAddress,
                        onValueChange = { kosAddress = it },
                        placeholder = "Masukkan alamat lengkap kost"
                    )
                }
                
                // Email
                item {
                    FormField(
                        label = "Email",
                        value = ownerEmail,
                        onValueChange = { ownerEmail = it },
                        placeholder = "Masukkan email pendaftar"
                    )
                }
                
                // WhatsApp
                item {
                    FormField(
                        label = "Whatsapp",
                        value = ownerWhatsapp,
                        onValueChange = { ownerWhatsapp = it },
                        placeholder = "Masukkan nomor WhatsApp"
                    )
                }
                
                // Instagram
                item {
                    FormField(
                        label = "Instagram",
                        value = ownerInstagram,
                        onValueChange = { ownerInstagram = it },
                        placeholder = "Masukkan username Instagram"
                    )
                }
                
                // Telepon
                item {
                    FormField(
                        label = "Telepon",
                        value = ownerPhone,
                        onValueChange = { ownerPhone = it },
                        placeholder = "Masukkan nomor telepon"
                    )
                }
                
                // Harga (Range)
                item {
                    PriceRangeSection(
                        minPrice = minPrice,
                        maxPrice = maxPrice,
                        onMinPriceChange = { minPrice = it },
                        onMaxPriceChange = { maxPrice = it }
                    )
                }
                
                // Fasilitas
                item {
                    FacilitiesSection(facilityOptions = facilityOptions)
                }
                
                // Upload Gambar
                item {
                    ImageUploadSection()
                }
                
                // Alamat Kos (Map)
                item {
                    MapLocationSection()
                }
                
                // Submit Button
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            // TODO: Handle form submission
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5876FF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Submit",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun AddKosHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF1B2633)
            )
        }
        
        Text(
            text = "Pendaftaran Kost",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2633)
            )
        )
        
        IconButton(onClick = { /* TODO: Edit options */ }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color(0xFF1B2633)
            )
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1B2633)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF9E9E9E)
                    )
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color(0xFF5876FF),
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Composable
private fun PriceRangeSection(
    minPrice: String,
    maxPrice: String,
    onMinPriceChange: (String) -> Unit,
    onMaxPriceChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Harga",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1B2633)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = minPrice,
                onValueChange = onMinPriceChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Harga minimum",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF9E9E9E)
                        )
                    )
                },
                prefix = {
                    Text(
                        text = "Rp ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF1B2633)
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFF5876FF),
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
            
            Text(
                text = "-",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF1B2633)
                ),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            
            OutlinedTextField(
                value = maxPrice,
                onValueChange = onMaxPriceChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Harga maksimum",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF9E9E9E)
                        )
                    )
                },
                prefix = {
                    Text(
                        text = "Rp ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF1B2633)
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFF5876FF),
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }
    }
}

@Composable
private fun FacilitiesSection(
    facilityOptions: MutableList<KosFacilityOption>
) {
    Column {
        Text(
            text = "Fasilitas",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1B2633)
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Grid of facilities with checkboxes
        facilityOptions.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { facility ->
                    FacilityCheckboxItem(
                        facility = facility,
                        onCheckedChange = { isChecked ->
                            facility.isSelected = isChecked
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Fill empty space if odd number of items
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FacilityCheckboxItem(
    facility: KosFacilityOption,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!facility.isSelected) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = facility.isSelected,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF5876FF),
                uncheckedColor = Color(0xFF9E9E9E)
            )
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = facility.icon,
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = facility.name,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF1B2633)
            )
        )
    }
}

@Composable
private fun ImageUploadSection() {
    Column {
        Text(
            text = "Upload Gambar Kos",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1B2633)
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Add image button
            item {
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { /* TODO: Handle image upload */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Image",
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tambah",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF9E9E9E)
                                )
                            )
                        }
                    }
                }
            }
            
            // Placeholder for uploaded images
            items(3) { index ->
                Card(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8E8E8)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📷",
                            fontSize = 32.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MapLocationSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Penanda Alamat Kos di Map",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1B2633)
                )
            )
            
            Text(
                text = "Pilih Lokasi",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF5876FF),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable { /* TODO: Open map picker */ }
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Map placeholder
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clickable { /* TODO: Open map picker */ },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFF5876FF),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap untuk memilih lokasi di map",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6B7280)
                        )
                    )
                }
            }
        }
    }
}
