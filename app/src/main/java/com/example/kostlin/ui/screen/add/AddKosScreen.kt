package com.example.kostlin.ui.screen.add

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kostlin.ui.components.BottomNavigation
import com.example.kostlin.ui.components.BottomNavRoute
import com.example.kostlin.ui.components.MapLocationPickerDialog

data class KosFacilityOption(
    val name: String,
    val icon: String,
    val isSelected: Boolean = false
)

// Store both Bitmap for upload and ImageBitmap for display
data class SelectedKosImage(
    val bitmap: Bitmap,
    val imageBitmap: ImageBitmap
)

enum class KosType(val displayName: String) {
    PUTRA("Kos Putra"),
    PUTRI("Kos Putri"),
    CAMPUR("Kos Campur")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddKosScreen(
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit = {},
    addKosViewModel: AddKosViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by addKosViewModel.uiState.collectAsState()
    
    // Form state - aligned with database schema
    var kosName by remember { mutableStateOf("") }
    var kosDescription by remember { mutableStateOf("") }
    var kosAddress by remember { mutableStateOf("") }
    var kosCity by remember { mutableStateOf("") }
    var pricePerMonth by remember { mutableStateOf("") }
    var selectedKosType by remember { mutableStateOf<KosType?>(null) }
    var selectedLatitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var selectedLongitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var showLocationDialog by remember { mutableStateOf(false) }
    
    val selectedImages = remember { mutableStateListOf<SelectedKosImage>() }
    
    // Handle success reset
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            kosName = ""
            kosDescription = ""
            kosAddress = ""
            kosCity = ""
            pricePerMonth = ""
            selectedKosType = null
            selectedLatitude = null
            selectedLongitude = null
            selectedImages.clear()
        }
    }

    val facilityOptions = remember {
        mutableStateListOf(
            KosFacilityOption("WiFi", "📶"),
            KosFacilityOption("AC", "❄️"),
            KosFacilityOption("Kamar Mandi Dalam", "🚿"),
            KosFacilityOption("Dapur Bersama", "🍳"),
            KosFacilityOption("Parkir Motor", "🏍️"),
            KosFacilityOption("Security 24 Jam", "🛡️"),
            KosFacilityOption("Laundry", "👕"),
            KosFacilityOption("Lemari", "🗄️")
        )
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            runCatching {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }.getOrNull()?.let { bitmap ->
                selectedImages.add(SelectedKosImage(bitmap, bitmap.asImageBitmap()))
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            selectedImages.add(SelectedKosImage(it, it.asImageBitmap()))
        }
    }

    fun toggleFacility(option: KosFacilityOption) {
        val index = facilityOptions.indexOfFirst { it.name == option.name }
        if (index >= 0) {
            facilityOptions[index] = facilityOptions[index].copy(isSelected = !option.isSelected)
        }
    }

    fun isFormValid(): Boolean {
        return kosName.isNotBlank() && 
               kosAddress.isNotBlank() && 
               kosCity.isNotBlank() && 
               pricePerMonth.isNotBlank() && 
               selectedKosType != null
    }

    fun resetForm() {
        kosName = ""
        kosDescription = ""
        kosAddress = ""
        kosCity = ""
        pricePerMonth = ""
        selectedKosType = null
        selectedLatitude = null
        selectedLongitude = null
        selectedImages.clear()
        for (i in facilityOptions.indices) {
            facilityOptions[i] = facilityOptions[i].copy(isSelected = false)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White, CircleShape)
                            .shadow(4.dp, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1B2633)
                        )
                    }
                    
                    Text(
                        text = "Daftarkan Kos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2633)
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(44.dp))
                }
            }

            // Basic Info Section
            item {
                SectionCard(title = "Informasi Dasar") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Nama Kos
                        StyledTextField(
                            value = kosName,
                            onValueChange = { kosName = it },
                            label = "Nama Kos",
                            placeholder = "Contoh: Kos Harmoni Sejahtera"
                        )
                        
                        // Deskripsi
                        StyledTextField(
                            value = kosDescription,
                            onValueChange = { kosDescription = it },
                            label = "Deskripsi",
                            placeholder = "Jelaskan keunggulan kos Anda...",
                            singleLine = false,
                            minLines = 3
                        )
                        
                        // Tipe Kos
                        Text(
                            text = "Tipe Kos",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            KosType.entries.forEach { type ->
                                val isSelected = selectedKosType == type
                                val backgroundColor by animateColorAsState(
                                    targetValue = if (isSelected) Color(0xFF5876FF) else Color.White,
                                    animationSpec = tween(200),
                                    label = "typeColor"
                                )
                                
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { selectedKosType = type }
                                        .border(
                                            width = if (isSelected) 0.dp else 1.dp,
                                            color = Color(0xFFE5E7EB),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    color = backgroundColor,
                                    shadowElevation = if (isSelected) 4.dp else 0.dp
                                ) {
                                    Text(
                                        text = type.displayName,
                                        modifier = Modifier
                                            .padding(vertical = 14.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else Color(0xFF374151),
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Location Section
            item {
                SectionCard(title = "Lokasi") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Alamat
                        StyledTextField(
                            value = kosAddress,
                            onValueChange = { kosAddress = it },
                            label = "Alamat Lengkap",
                            placeholder = "Jl. Contoh No. 123, RT/RW"
                        )
                        
                        // Kota
                        StyledTextField(
                            value = kosCity,
                            onValueChange = { kosCity = it },
                            label = "Kota",
                            placeholder = "Contoh: Padang"
                        )
                        
                        // Map Location Picker
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLocationDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF0F4FF)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = CircleShape,
                                    color = Color(0xFF5876FF)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (selectedLatitude != null) "Lokasi Dipilih" else "Pilih Lokasi di Peta",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1B2633)
                                        )
                                    )
                                    if (selectedLatitude != null && selectedLongitude != null) {
                                        Text(
                                            text = String.format("%.5f, %.5f", selectedLatitude, selectedLongitude),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF6B7280)
                                            )
                                        )
                                    } else {
                                        Text(
                                            text = "Tap untuk menentukan koordinat",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF6B7280)
                                            )
                                        )
                                    }
                                }
                                
                                if (selectedLatitude != null) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Price Section
            item {
                SectionCard(title = "Harga") {
                    StyledTextField(
                        value = pricePerMonth,
                        onValueChange = { pricePerMonth = it.filter { char -> char.isDigit() } },
                        label = "Harga per Bulan",
                        placeholder = "500000",
                        prefix = "Rp ",
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            // Facilities Section
            item {
                SectionCard(title = "Fasilitas") {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        facilityOptions.forEach { facility ->
                            FilterChip(
                                selected = facility.isSelected,
                                onClick = { toggleFacility(facility) },
                                label = {
                                    Text(
                                        text = "${facility.icon} ${facility.name}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                leadingIcon = if (facility.isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF5876FF).copy(alpha = 0.15f),
                                    selectedLabelColor = Color(0xFF5876FF),
                                    selectedLeadingIconColor = Color(0xFF5876FF)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }
            }

            // Image Upload Section
            item {
                SectionCard(title = "Foto Kos") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Upload buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            UploadButton(
                                icon = Icons.Default.PhotoLibrary,
                                text = "Galeri",
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f)
                            )
                            UploadButton(
                                icon = Icons.Default.CameraAlt,
                                text = "Kamera",
                                onClick = { cameraLauncher.launch(null) },
                                modifier = Modifier.weight(1f),
                                isPrimary = true
                            )
                        }
                        
                        // Image preview
                        if (selectedImages.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(selectedImages.size) { index ->
                                    Box {
                                        Card(
                                            modifier = Modifier.size(100.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            elevation = CardDefaults.cardElevation(2.dp)
                                        ) {
                                            Image(
                                                bitmap = selectedImages[index].imageBitmap,
                                                contentDescription = "Foto $index",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        
                                        IconButton(
                                            onClick = { selectedImages.removeAt(index) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(24.dp)
                                                .background(Color.Red.copy(alpha = 0.9f), CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada foto yang dipilih",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF9CA3AF)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Submit Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        if (isFormValid()) {
                            val selectedFacilities = facilityOptions
                                .filter { it.isSelected }
                                .map { Pair(it.name, it.icon) }
                            
                            // Extract original Bitmap for upload
                            val bitmaps = selectedImages.map { it.bitmap }
                            
                            addKosViewModel.createKosWithImages(
                                name = kosName,
                                description = kosDescription.ifBlank { null },
                                address = kosAddress,
                                city = kosCity,
                                latitude = selectedLatitude,
                                longitude = selectedLongitude,
                                pricePerMonth = pricePerMonth.toIntOrNull() ?: 0,
                                type = selectedKosType?.name ?: "CAMPUR",
                                facilities = selectedFacilities,
                                images = bitmaps
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = isFormValid() && !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5876FF),
                        disabledContainerColor = Color(0xFFD1D5DB)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Daftarkan Kos",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
                
                // Show success message first (priority)
                if (uiState.isSuccess) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFD1FAE5)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF10B981)
                            )
                            Text(
                                text = "Data kos berhasil disimpan!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
                
                // Warning message (for partial success - image upload failed)
                uiState.warningMessage?.let { warning ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFEF3C7)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color(0xFFD97706)
                            )
                            Text(
                                text = warning,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
                
                // Error message (only show when NOT success)
                if (!uiState.isSuccess) {
                    uiState.errorMessage?.let { error ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFEE2E2)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626)
                                )
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF991B1B),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showLocationDialog) {
            MapLocationPickerDialog(
                initialLatitude = selectedLatitude,
                initialLongitude = selectedLongitude,
                onDismiss = { showLocationDialog = false },
                onConfirm = { lat, lon ->
                    selectedLatitude = lat
                    selectedLongitude = lon
                    showLocationDialog = false
                }
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2633)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF374151)
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
                        color = Color(0xFF9CA3AF)
                    )
                )
            },
            prefix = prefix?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151)
                        )
                    )
                }
            },
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5876FF),
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color(0xFFFAFAFA)
            )
        )
    }
}

@Composable
private fun UploadButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) Color(0xFF5876FF) else Color(0xFFF3F4F6)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isPrimary) Color.White else Color(0xFF374151),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = if (isPrimary) Color.White else Color(0xFF374151)
            )
        )
    }
}

@Composable
fun LocationPickerDialog(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit
) {
    var latitude by remember { mutableStateOf(initialLatitude?.toString() ?: "") }
    var longitude by remember { mutableStateOf(initialLongitude?.toString() ?: "") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Masukkan Koordinat",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    placeholder = { Text("-0.9284") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    placeholder = { Text("100.4284") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lat = latitude.toDoubleOrNull()
                    val lon = longitude.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        onConfirm(lat, lon)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5876FF)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    color = Color(0xFF6B7280)
                )
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
