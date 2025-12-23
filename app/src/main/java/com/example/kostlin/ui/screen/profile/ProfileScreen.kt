package com.example.kostlin.ui.screen.profile

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kostlin.domain.model.Booking
import com.example.kostlin.domain.model.Kos
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.platform.LocalContext
import com.example.kostlin.ui.screen.booking.BookingDetailScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    onBackClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogout: () -> Unit,
    profileViewModel: ProfileViewModel,
    onEditKos: (Kos) -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val uiState by profileViewModel.uiState.collectAsState()
    val tabs = listOf("Info", "Kos Saya", "Riwayat", "Request Masuk")
    
    // Delete confirmation dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var kosToDelete by remember { mutableStateOf<Kos?>(null) }
    
    // Rating dialog state
    var showRatingDialog by remember { mutableStateOf(false) }
    var bookingToRate by remember { mutableStateOf<Booking?>(null) }
    var selectedRating by remember { mutableStateOf(5) }
    var ratingComment by remember { mutableStateOf("") }
    
    // Booking detail screen state
    var selectedBookingDetail by remember { mutableStateOf<Booking?>(null) }
    
    // Change password dialog state
    var showPasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    // Fetch user profile on first load
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }
    
    LaunchedEffect(selectedTabIndex) {
        when (selectedTabIndex) {
            0 -> profileViewModel.fetchUserProfile()
            1 -> profileViewModel.fetchMyKos()
            2 -> profileViewModel.fetchBookingHistory()
            3 -> profileViewModel.fetchBookingRequests()
        }
    }
    
    // Snackbar for success/error messages
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            profileViewModel.clearMessages()
        }
    }
    
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar("Error: $message")
            profileViewModel.clearMessages()
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog && kosToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                kosToDelete = null
            },
            title = { Text("Hapus Kos") },
            text = { Text("Apakah Anda yakin ingin menghapus kos \"${kosToDelete!!.name}\"? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        kosToDelete?.let { kos ->
                            profileViewModel.deleteKos(kos.id.toIntOrNull() ?: 0)
                        }
                        showDeleteDialog = false
                        kosToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { 
                    showDeleteDialog = false
                    kosToDelete = null
                }) {
                    Text("Batal")
                }
            }
        )
    }
    
    // Rating dialog
    if (showRatingDialog && bookingToRate != null) {
        AlertDialog(
            onDismissRequest = { 
                showRatingDialog = false
                bookingToRate = null
                selectedRating = 5
                ratingComment = ""
            },
            title = { Text("Beri Rating") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Kos: ${bookingToRate?.kos?.name ?: ""}")
                    
                    // Star rating row
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..5).forEach { star ->
                            Text(
                                text = if (star <= selectedRating) "⭐" else "☆",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.clickable { selectedRating = star }
                            )
                        }
                    }
                    
                    Text("Rating: $selectedRating/5", style = MaterialTheme.typography.bodyMedium)
                    
                    // Comment input
                    OutlinedTextField(
                        value = ratingComment,
                        onValueChange = { ratingComment = it },
                        label = { Text("Komentar (opsional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        bookingToRate?.let { booking ->
                            profileViewModel.createReview(
                                kosId = booking.kosId,
                                rating = selectedRating.toDouble(),
                                comment = ratingComment.ifBlank { null }
                            )
                        }
                        showRatingDialog = false
                        bookingToRate = null
                        selectedRating = 5
                        ratingComment = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2C94C))
                ) {
                    Text("Kirim Rating", color = Color.Black)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { 
                    showRatingDialog = false
                    bookingToRate = null
                    selectedRating = 5
                    ratingComment = ""
                }) {
                    Text("Batal")
                }
            }
        )
    }
    
    // Change password dialog
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = false
                currentPassword = ""
                newPassword = ""
                confirmPassword = ""
            },
            title = { Text("Ubah Password") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Password Saat Ini") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Password Baru") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Konfirmasi Password Baru") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    if (newPassword != confirmPassword && confirmPassword.isNotEmpty()) {
                        Text(
                            text = "Password baru tidak cocok",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword == confirmPassword && newPassword.length >= 6) {
                            profileViewModel.changePassword(currentPassword, newPassword)
                            showPasswordDialog = false
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                        }
                    },
                    enabled = currentPassword.isNotEmpty() && 
                              newPassword.length >= 6 && 
                              newPassword == confirmPassword,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5876FF))
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { 
                    showPasswordDialog = false
                    currentPassword = ""
                    newPassword = ""
                    confirmPassword = ""
                }) {
                    Text("Batal")
                }
            }
        )
    }

    // Show BookingDetailScreen when a booking is selected
    val context = LocalContext.current
    if (selectedBookingDetail != null) {
        BookingDetailScreen(
            booking = selectedBookingDetail!!,
            onBackClick = { selectedBookingDetail = null },
            onWhatsAppContact = { phoneNumber ->
                val formattedNumber = phoneNumber.replace("[^0-9]".toRegex(), "")
                val whatsappUrl = "https://wa.me/$formattedNumber"
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                    data = android.net.Uri.parse(whatsappUrl)
                }
                context.startActivity(intent)
            }
        )
    } else {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Profil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FF))
                .padding(innerPadding)
        ) {
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFF5876FF),
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
            
            // Content - use ViewModel data for user info
            when (selectedTabIndex) {
                0 -> UserInfoSection(
                    userName = uiState.userName.ifBlank { userName },
                    userEmail = uiState.userEmail.ifBlank { userEmail },
                    userPhone = uiState.userPhone,
                    onChangePasswordClick = { showPasswordDialog = true },
                    onLogout = onLogout
                )
                1 -> OwnedKosSection(
                    kosList = uiState.ownedKos,
                    isLoading = uiState.isLoadingKos,
                    onKosClick = { /* TODO: Navigate to detail */ },
                    onEditClick = { kos -> onEditKos(kos) },
                    onDeleteClick = { kos -> 
                        kosToDelete = kos
                        showDeleteDialog = true
                    },
                    onToggleClick = { kos -> 
                        profileViewModel.toggleKosActive(
                            kosId = kos.id.toIntOrNull() ?: 0,
                            isCurrentlyActive = kos.isActive ?: true
                        )
                    }
                )
                2 -> BookingHistorySection(
                    bookings = uiState.bookingHistory, 
                    isLoading = uiState.isLoadingHistory,
                    onBookingClick = { booking ->
                        selectedBookingDetail = booking
                    },
                    onRate = { booking ->
                        bookingToRate = booking
                        showRatingDialog = true
                    }
                )
                3 -> BookingRequestsSection(
                    requests = uiState.bookingRequests,
                    isLoading = uiState.isLoadingRequests,
                    isUpdating = uiState.isUpdating,
                    onConfirm = { profileViewModel.confirmBooking(it) },
                    onCancel = { profileViewModel.cancelBooking(it) }
                )
            }
        }
    }
    } // end else (when BookingDetailScreen is not shown)
}

@Composable
private fun UserInfoSection(
    userName: String, 
    userEmail: String, 
    userPhone: String,
    onChangePasswordClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Avatar
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color(0xFF5876FF)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = userName.ifBlank { "User" },
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        
        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280))
        )
        
        if (userPhone.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = userPhone,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280))
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onChangePasswordClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5876FF)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ubah Password")
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Keluar")
        }
    }
}

@Composable
private fun OwnedKosSection(
    kosList: List<Kos>,
    isLoading: Boolean,
    onKosClick: (Kos) -> Unit,
    onEditClick: (Kos) -> Unit,
    onDeleteClick: (Kos) -> Unit,
    onToggleClick: (Kos) -> Unit
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF5876FF))
        }
    } else if (kosList.isEmpty()) {
        EmptyState(emoji = "🏠", message = "Anda belum memiliki kos")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(kosList) { kos ->
                OwnedKosCard(
                    kos = kos,
                    onClick = { onKosClick(kos) },
                    onEditClick = { onEditClick(kos) },
                    onDeleteClick = { onDeleteClick(kos) },
                    onToggleClick = { onToggleClick(kos) }
                )
            }
        }
    }
}

@Composable
private fun OwnedKosCard(
    kos: Kos,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleClick: () -> Unit
) {
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val isActive = kos.isActive ?: true
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActive) Color.White else Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                // Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    if (kos.thumbnailUrl != null) {
                        AsyncImage(
                            model = kos.thumbnailUrl,
                            contentDescription = kos.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(Color(0xFF5876FF), Color(0xFF8B9DFF)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Home, "Kos", tint = Color.White)
                        }
                    }
                    
                    // Active/Inactive badge
                    if (!isActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "NONAKTIF",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = kos.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        // Status badge
                        StatusBadge(
                            text = if (isActive) "Aktif" else "Nonaktif",
                            color = if (isActive) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, "Location", Modifier.size(12.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = kos.city,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = priceFormatter.format(kos.pricePerMonth).replace(",00", "") + "/bulan",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5876FF)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Toggle Active button
                OutlinedButton(
                    onClick = onToggleClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isActive) Color(0xFFFF9800) else Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        if (isActive) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isActive) "Nonaktifkan" else "Aktifkan", style = MaterialTheme.typography.labelSmall)
                }
                
                // Edit button
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5876FF))
                ) {
                    Icon(Icons.Default.Edit, "Edit", Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", style = MaterialTheme.typography.labelSmall)
                }
                
                // Delete button
                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336))
                ) {
                    Icon(Icons.Default.Delete, "Hapus", Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun BookingHistorySection(
    bookings: List<Booking>, 
    isLoading: Boolean,
    onBookingClick: (Booking) -> Unit = {},
    onRate: (Booking) -> Unit = {}
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF5876FF))
        }
    } else if (bookings.isEmpty()) {
        EmptyState(emoji = "📋", message = "Belum ada riwayat booking")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bookings) { booking ->
                BookingCard(
                    booking = booking, 
                    showUser = false, 
                    showActions = false, 
                    showRating = booking.status == "confirmed" || booking.status == "completed",
                    onConfirm = {}, 
                    onCancel = {},
                    onRate = { onRate(booking) },
                    onClick = { onBookingClick(booking) }
                )
            }
        }
    }
}

@Composable
private fun BookingRequestsSection(
    requests: List<Booking>,
    isLoading: Boolean,
    isUpdating: Boolean,
    onConfirm: (Int) -> Unit,
    onCancel: (Int) -> Unit
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF5876FF))
        }
    } else if (requests.isEmpty()) {
        EmptyState(emoji = "📩", message = "Tidak ada request booking masuk")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(requests) { booking ->
                BookingCard(
                    booking = booking,
                    showUser = true,
                    showActions = booking.status == "pending",
                    onConfirm = { onConfirm(booking.id) },
                    onCancel = { onCancel(booking.id) },
                    isUpdating = isUpdating
                )
            }
        }
    }
}

@Composable
private fun BookingCard(
    booking: Booking,
    showUser: Boolean,
    showActions: Boolean,
    showRating: Boolean = false,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onRate: () -> Unit = {},
    onClick: () -> Unit = {},
    isUpdating: Boolean = false
) {
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Kos info
            Row {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    val imageUrl = booking.kos?.thumbnailUrl
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = booking.kos?.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(Color(0xFF5876FF), Color(0xFF8B9DFF)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏠", style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.kos?.name ?: "Kos #${booking.kosId}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Booking info badges
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatusBadge(
                            text = if (booking.bookingType == "yearly") "Tahunan" else "Bulanan",
                            color = Color(0xFF5876FF)
                        )
                        StatusBadge(
                            text = "${booking.roomQuantity} Kamar",
                            color = Color(0xFF6B7280)
                        )
                    }
                    
                    Text(
                        text = priceFormatter.format(booking.totalPrice).replace(",00", ""),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5876FF)
                        )
                    )
                }
                
                // Status badge
                StatusBadge(
                    text = when (booking.status) {
                        "confirmed" -> "Dikonfirmasi"
                        "pending" -> "Menunggu"
                        "cancelled" -> "Dibatalkan"
                        else -> booking.status
                    },
                    color = when (booking.status) {
                        "confirmed" -> Color(0xFF4CAF50)
                        "pending" -> Color(0xFFFF9800)
                        "cancelled" -> Color(0xFFF44336)
                        else -> Color.Gray
                    }
                )
            }
            
            // User info (for requests - show booker info)
            if (showUser && booking.user != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5F5F5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, "User", Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Pemesan: ${booking.user.name ?: "Unknown"}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                            if (!booking.user.phone.isNullOrBlank()) {
                                Text(
                                    text = "📱 ${booking.user.phone}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280))
                                )
                            }
                        }
                    }
                }
            }
            
            // Action buttons
            if (showActions) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Close, "Cancel", Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tolak")
                    }
                    
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Check, "Confirm", Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Konfirmasi")
                        }
                    }
                }
            }
            
            // Rating button for confirmed/completed bookings
            if (showRating) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRate,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2C94C)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("⭐ Beri Rating", color = Color.Black, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = color
            )
        )
    }
}

@Composable
private fun EmptyState(emoji: String, message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, style = MaterialTheme.typography.displayMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF6B7280))
            )
        }
    }
}
