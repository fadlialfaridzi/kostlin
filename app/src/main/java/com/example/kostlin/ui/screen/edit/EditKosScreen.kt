package com.example.kostlin.ui.screen.edit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kostlin.domain.model.Kos
import com.example.kostlin.ui.components.MapLocationPickerDialog

data class KosFacilityOption(
    val name: String,
    val icon: String,
    val isSelected: Boolean = false
)

enum class KosType(val displayName: String) {
    PUTRA("Kos Putra"),
    PUTRI("Kos Putri"),
    CAMPUR("Kos Campur")
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditKosScreen(
    kos: Kos,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    editKosViewModel: EditKosViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by editKosViewModel.uiState.collectAsState()
    
    // Pre-fill form state with existing kos data
    var kosName by remember { mutableStateOf(kos.name) }
    var kosDescription by remember { mutableStateOf(kos.description) }
    var kosAddress by remember { mutableStateOf(kos.address) }
    var kosCity by remember { mutableStateOf(kos.city) }
    var pricePerMonth by remember { mutableStateOf(kos.pricePerMonth.toString()) }
    var selectedKosType by remember { 
        mutableStateOf(
            when (kos.type.name.lowercase()) {
                "putra" -> KosType.PUTRA
                "putri" -> KosType.PUTRI
                else -> KosType.CAMPUR
            }
        )
    }
    var selectedLatitude by rememberSaveable { mutableStateOf(kos.latitude) }
    var selectedLongitude by rememberSaveable { mutableStateOf(kos.longitude) }
    var showLocationDialog by remember { mutableStateOf(false) }
    
    // Handle success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSaveSuccess()
        }
    }

    val facilityOptions = remember {
        val existingFacilityNames = kos.facilities.map { it.name.lowercase() }
        mutableStateListOf(
            KosFacilityOption("WiFi", "📶", existingFacilityNames.contains("wifi")),
            KosFacilityOption("AC", "❄️", existingFacilityNames.contains("ac")),
            KosFacilityOption("Kamar Mandi Dalam", "🚿", existingFacilityNames.any { it.contains("kamar mandi") }),
            KosFacilityOption("Dapur Bersama", "🍳", existingFacilityNames.any { it.contains("dapur") }),
            KosFacilityOption("Parkir Motor", "🏍️", existingFacilityNames.any { it.contains("parkir") }),
            KosFacilityOption("Security 24 Jam", "🛡️", existingFacilityNames.any { it.contains("security") }),
            KosFacilityOption("Laundry", "👕", existingFacilityNames.contains("laundry")),
            KosFacilityOption("Lemari", "🗄️", existingFacilityNames.contains("lemari"))
        )
    }

    // Location picker dialog
    if (showLocationDialog) {
        MapLocationPickerDialog(
            initialLatitude = selectedLatitude ?: -0.9471,
            initialLongitude = selectedLongitude ?: 100.4172,
            onConfirm = { lat, lng ->
                selectedLatitude = lat
                selectedLongitude = lng
                showLocationDialog = false
            },
            onDismiss = { showLocationDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Kos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F9FF))
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Current Images preview
                if (kos.images.isNotEmpty()) {
                    item {
                        Text("Gambar Saat Ini", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            kos.images.take(3).forEach { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Kos Image",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
                
                // Form fields
                item {
                    OutlinedTextField(
                        value = kosName,
                        onValueChange = { kosName = it },
                        label = { Text("Nama Kos *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5876FF),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = kosDescription,
                        onValueChange = { kosDescription = it },
                        label = { Text("Deskripsi") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5876FF),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = kosAddress,
                        onValueChange = { kosAddress = it },
                        label = { Text("Alamat *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5876FF),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = kosCity,
                        onValueChange = { kosCity = it },
                        label = { Text("Kota *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5876FF),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = pricePerMonth,
                        onValueChange = { pricePerMonth = it.filter { c -> c.isDigit() } },
                        label = { Text("Harga per Bulan (Rp) *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5876FF),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                }

                // Kos Type
                item {
                    Text("Tipe Kos *", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KosType.entries.forEach { type ->
                            FilterChip(
                                selected = selectedKosType == type,
                                onClick = { selectedKosType = type },
                                label = { Text(type.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF5876FF),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Location
                item {
                    Text("Lokasi", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLocationDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, "Location", tint = Color(0xFF5876FF))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    if (selectedLatitude != null) "Lokasi Dipilih" else "Pilih Lokasi di Peta",
                                    fontWeight = FontWeight.Medium
                                )
                                if (selectedLatitude != null) {
                                    Text(
                                        "%.4f, %.4f".format(selectedLatitude, selectedLongitude),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                // Facilities
                item {
                    Text("Fasilitas", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        facilityOptions.forEachIndexed { index, facility ->
                            FilterChip(
                                selected = facility.isSelected,
                                onClick = {
                                    facilityOptions[index] = facility.copy(isSelected = !facility.isSelected)
                                },
                                label = { Text("${facility.icon} ${facility.name}") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF5876FF),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Error message
                if (uiState.error != null) {
                    item {
                        Text(
                            text = uiState.error!!,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Save button
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val selectedFacilities = facilityOptions.filter { it.isSelected }.map { it.name }
                            editKosViewModel.updateKos(
                                kosId = kos.id.toIntOrNull() ?: 0,
                                name = kosName,
                                description = kosDescription,
                                address = kosAddress,
                                city = kosCity,
                                pricePerMonth = pricePerMonth.toIntOrNull() ?: 0,
                                type = selectedKosType?.name?.lowercase() ?: "campur",
                                latitude = selectedLatitude,
                                longitude = selectedLongitude,
                                facilities = selectedFacilities
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5876FF)),
                        enabled = !uiState.isLoading && kosName.isNotBlank() && kosAddress.isNotBlank() && kosCity.isNotBlank() && pricePerMonth.isNotBlank()
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Check, "Save", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}
