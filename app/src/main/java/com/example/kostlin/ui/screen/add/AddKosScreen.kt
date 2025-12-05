package com.example.kostlin.ui.screen.add

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kostlin.data.model.KosType
import com.example.kostlin.di.AppModule
import com.example.kostlin.ui.components.BottomNavigation
import com.example.kostlin.ui.components.BottomNavRoute
import com.example.kostlin.ui.viewmodel.AddKosViewModel

data class KosFacilityOption(
    val name: String,
    val icon: String,
    val isSelected: Boolean = false
)

data class KosFormSnapshot(
    val name: String,
    val address: String,
    val contactEmail: String,
    val contactWhatsapp: String,
    val contactInstagram: String,
    val contactPhone: String,
    val minPrice: String,
    val maxPrice: String,
    val facilities: List<String>,
    val imageCount: Int,
    val latitude: Double?,
    val longitude: Double?
)

@Composable
fun AddKosScreen(
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit = {},
    onKosCreated: () -> Unit = {}, // Callback ketika kos berhasil dibuat
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val kosRepository = remember { AppModule.getKosRepository() }
    val viewModel: AddKosViewModel = viewModel { AddKosViewModel(kosRepository) }
    val uiState by viewModel.uiState.collectAsState()
    
    var kosName by remember { mutableStateOf("") }
    var kosAddress by remember { mutableStateOf("") }
    var ownerEmail by remember { mutableStateOf("") }
    var ownerWhatsapp by remember { mutableStateOf("") }
    var ownerInstagram by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var kosType by remember { mutableStateOf("CAMPUR") } // Default type
    var description by remember { mutableStateOf("") }
    var showLocationDialog by remember { mutableStateOf(false) }
    var selectedLatitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var selectedLongitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var temporarySaveMessage by rememberSaveable { mutableStateOf<String?>(null) }
    
    val facilityOptions = remember {
        mutableStateListOf(
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
    
    val selectedImages = remember { mutableStateListOf<ImageBitmap>() }
    
    // Handle success - clear form and show message
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            // Clear form
            kosName = ""
            kosAddress = ""
            ownerEmail = ""
            ownerWhatsapp = ""
            ownerInstagram = ""
            ownerPhone = ""
            minPrice = ""
            maxPrice = ""
            description = ""
            kosType = "CAMPUR"
            facilityOptions.replaceAll { it.copy(isSelected = false) }
            selectedImages.clear()
            selectedLatitude = null
            selectedLongitude = null
            
            // Call callback untuk refresh dashboard
            onKosCreated()
            
            // Clear success message after 3 seconds
            kotlinx.coroutines.delay(3000)
            viewModel.clearSuccess()
        }
    }


    val savedKosDrafts = remember { mutableStateListOf<KosFormSnapshot>() }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            runCatching {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()?.let { bitmap ->
                selectedImages.add(bitmap)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.asImageBitmap()?.let { image ->
            selectedImages.add(image)
        }
    }

    fun toggleFacility(option: KosFacilityOption, isSelected: Boolean) {
        val index = facilityOptions.indexOfFirst { it.name == option.name }
        if (index >= 0) {
            facilityOptions[index] = facilityOptions[index].copy(isSelected = isSelected)
        }
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
                    FacilitiesSection(
                        facilityOptions = facilityOptions,
                        onToggle = ::toggleFacility
                    )
                }
                
                // Upload Gambar
                item {
                    ImageUploadSection(
                        images = selectedImages,
                        onAddFromGallery = { galleryLauncher.launch("image/*") },
                        onAddFromCamera = { cameraLauncher.launch(null) },
                        onRemoveImage = { index ->
                            if (index in selectedImages.indices) {
                                selectedImages.removeAt(index)
                            }
                        }
                    )
                }
                
                // Alamat Kos (Map)
                item {
                    MapLocationSection(
                        latitude = selectedLatitude,
                        longitude = selectedLongitude,
                        onPickLocation = { showLocationDialog = true }
                    )
                }
                
                // Type Selection
                item {
                    Column {
                        Text(
                            text = "Tipe Kos",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1B2633)
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("PUTRA", "PUTRI", "CAMPUR").forEach { type ->
                                Button(
                                    onClick = { kosType = type },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (kosType == type) Color(0xFF5876FF) else Color(0xFFF5F5F5)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = type,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (kosType == type) Color.White else Color(0xFF1B2633)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Description
                item {
                    Column {
                        Text(
                            text = "Deskripsi",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1B2633)
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Masukkan deskripsi kos",
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
                            maxLines = 4
                        )
                    }
                }
                
                // Submit Button
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Error message
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFEF4444),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }
                    
                    // Success message
                    if (uiState.successMessage != null) {
                        Text(
                            text = uiState.successMessage!!,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF10B981),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }
                    
                    Button(
                        onClick = {
                            // Validation
                            if (kosName.isBlank()) {
                                viewModel.clearError()
                                return@Button
                            }
                            if (kosAddress.isBlank()) {
                                viewModel.clearError()
                                return@Button
                            }
                            
                            val price = if (minPrice.isNotBlank()) {
                                minPrice.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                            } else {
                                0
                            }
                            
                            if (price <= 0) {
                                return@Button
                            }
                            
                            val facilities = facilityOptions.filter { it.isSelected }.map { it.name }
                            
                            viewModel.createKos(
                                name = kosName.trim(),
                                location = kosAddress.trim(),
                                pricePerMonth = price,
                                type = kosType,
                                description = description.takeIf { it.isNotBlank() },
                                ownerEmail = ownerEmail.takeIf { it.isNotBlank() },
                                ownerPhone = ownerPhone.takeIf { it.isNotBlank() },
                                latitude = selectedLatitude,
                                longitude = selectedLongitude,
                                facilities = facilities,
                                imageUrl = null // TODO: Implement image upload
                            ) {
                                // Success callback
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5876FF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isLoading
                    ) {
                        Text(
                            text = if (uiState.isLoading) "Mengirim..." else "Submit",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (savedKosDrafts.isNotEmpty()) {
                        SavedDraftsSection(drafts = savedKosDrafts)
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (showLocationDialog) {
            LocationPickerDialog(
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
    facilityOptions: List<KosFacilityOption>,
    onToggle: (KosFacilityOption, Boolean) -> Unit
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
                            onToggle(facility, isChecked)
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
private fun SavedDraftsSection(
    drafts: List<KosFormSnapshot>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Draft Tersimpan (${drafts.size})",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2633)
            )
        )

        drafts.take(3).forEach { draft ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = draft.name.ifBlank { "Nama belum diisi" },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2633)
                        )
                    )
                    Text(
                        text = draft.address.ifBlank { "Alamat belum diisi" },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6B7280)
                        )
                    )
                    Text(
                        text = "Kisaran harga: Rp ${draft.minPrice.ifBlank { "-" }} - Rp ${draft.maxPrice.ifBlank { "-" }}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF1B2633)
                        )
                    )
                    Text(
                        text = "Fasilitas terpilih: ${if (draft.facilities.isEmpty()) "Belum ada" else draft.facilities.joinToString()}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF1B2633)
                        )
                    )
                    Text(
                        text = "Foto: ${draft.imageCount} • Koordinat: ${
                            if (draft.latitude != null && draft.longitude != null) {
                                String.format("%.4f, %.4f", draft.latitude, draft.longitude)
                            } else {
                                "Belum ada"
                            }
                        }",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6B7280)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageUploadSection(
    images: List<ImageBitmap>,
    onAddFromGallery: () -> Unit,
    onAddFromCamera: () -> Unit,
    onRemoveImage: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Upload Gambar Kos",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1B2633)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onAddFromGallery,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF4FF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Pilih dari Galeri",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1B2633)
                    )
                )
            }

            Button(
                onClick = onAddFromCamera,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5876FF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Buka Kamera",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(images.size) { index ->
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { onRemoveImage(index) },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Image(
                        bitmap = images[index],
                        contentDescription = "Foto Kost $index",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            if (images.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .size(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada foto",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF9E9E9E),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapLocationSection(
    latitude: Double?,
    longitude: Double?,
    onPickLocation: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Penanda Alamat Kos di Map",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1B2633)
                    )
                )
                Text(
                    text = if (latitude != null && longitude != null) {
                        String.format("Lat: %.5f, Lng: %.5f", latitude, longitude)
                    } else {
                        "Belum ada koordinat"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF6B7280)
                    )
                )
            }
            
            Text(
                text = "Pilih Lokasi",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF5876FF),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable(onClick = onPickLocation)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clickable(onClick = onPickLocation),
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
                        text = if (latitude != null && longitude != null) {
                            "Tap untuk memperbarui lokasi"
                        } else {
                            "Tap untuk memilih lokasi di map"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationPickerDialog(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit
) {
    var latitudeText by rememberSaveable(initialLatitude) { mutableStateOf(initialLatitude?.toString().orEmpty()) }
    var longitudeText by rememberSaveable(initialLongitude) { mutableStateOf(initialLongitude?.toString().orEmpty()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tentukan Koordinat Kost",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = latitudeText,
                    onValueChange = {
                        latitudeText = it
                        errorMessage = null
                    },
                    label = { Text("Latitude") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = longitudeText,
                    onValueChange = {
                        longitudeText = it
                        errorMessage = null
                    },
                    label = { Text("Longitude") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFEF4444))
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val lat = latitudeText.toDoubleOrNull()
                val lon = longitudeText.toDoubleOrNull()
                if (lat != null && lon != null) {
                    onConfirm(lat, lon)
                } else {
                    errorMessage = "Masukkan angka yang valid"
                }
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
