package com.example.kostlin.ui.screen.favorite

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.rememberCoroutineScope
import com.example.kostlin.domain.model.FavoriteEntry
import com.example.kostlin.domain.model.FavoriteStatus
import com.example.kostlin.domain.model.Kos
import com.example.kostlin.domain.model.Booking
import com.example.kostlin.ui.components.BottomNavigation
import com.example.kostlin.ui.components.BottomNavRoute
import com.example.kostlin.ui.screen.booking.BookingViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.LaunchedEffect
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FavoriteKosScreen(
    onBackClick: () -> Unit,
    onKosClick: (Kos) -> Unit,
    favoriteViewModel: FavoriteViewModel,
    bookingViewModel: BookingViewModel,
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val favoriteState by favoriteViewModel.uiState.collectAsState()
    val bookingState by bookingViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    // Fetch bookings when opening History tab
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex == 1) {
            bookingViewModel.fetchBookings()
        }
    }

    val favorites = remember(searchQuery, selectedTabIndex, favoriteState.favorites) {
        val list = favoriteState.favorites.filter { it.status == FavoriteStatus.ACTIVE }
        if (searchQuery.isBlank()) {
            list
        } else {
            list.filter { entry ->
                entry.kos.name.contains(searchQuery, ignoreCase = true) ||
                entry.kos.address.contains(searchQuery, ignoreCase = true) ||
                entry.kos.city.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
        // Header
        FavoriteHeader(
            onBackClick = onBackClick
        )
        
        // Search Bar (only show for favorites tab)
        if (selectedTabIndex == 0) {
            SearchSection(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )
        }
        
        // Tab Section
        TabSection(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index ->
                selectedTabIndex = index
            },
            favoriteCount = favoriteState.favorites.count { it.status == FavoriteStatus.ACTIVE },
            historyCount = bookingState.bookings.size
        )
        
        // Content
        Box(modifier = Modifier.fillMaxWidth()) {
            if ((selectedTabIndex == 0 && favoriteState.isLoading) || 
                (selectedTabIndex == 1 && bookingState.isLoading)) {
                CircularProgressIndicator(color = Color(0xFF5876FF))
            }
        }

        if (selectedTabIndex == 0) {
            // Favorites Tab
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites) { favoriteEntry ->
                    FavoriteKosItem(
                        favoriteEntry = favoriteEntry,
                        onClick = { onKosClick(favoriteEntry.kos) },
                        onRemove = {
                            favoriteViewModel.removeFavorite(favoriteEntry.kos.id.toIntOrNull() ?: 0)
                        }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            // History Tab - Booking History
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (bookingState.bookings.isEmpty() && !bookingState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📋",
                                    style = MaterialTheme.typography.displayMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Belum ada riwayat booking",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color(0xFF6B7280)
                                    )
                                )
                            }
                        }
                    }
                } else {
                    items(bookingState.bookings) { booking ->
                        BookingHistoryItem(booking = booking)
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        }
    }
}

@Composable
private fun FavoriteHeader(
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
            text = "Kos Favorit",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2633)
            )
        )
        
        IconButton(onClick = { /* TODO: More options */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = Color(0xFF1B2633)
            )
        }
    }
}

@Composable
private fun SearchSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "Search...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF9E9E9E)
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF9E9E9E)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
        
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF5F5F5)
        ) {
            IconButton(onClick = { /* TODO: Filter */ }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = Color(0xFF1B2633)
                )
            }
        }
    }
}

@Composable
private fun TabSection(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    favoriteCount: Int,
    historyCount: Int
) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.padding(horizontal = 16.dp),
            containerColor = Color.Transparent,
            indicator = { },
            divider = { }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier
                    .background(
                        if (selectedTabIndex == 0) Color(0xFF5876FF) else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = "Favorit ($favoriteCount)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (selectedTabIndex == 0) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selectedTabIndex == 0) Color.White else Color(0xFF6B7280)
                    ),
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }
            
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier
                    .background(
                        if (selectedTabIndex == 1) Color(0xFF5876FF) else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = "History ($historyCount)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (selectedTabIndex == 1) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selectedTabIndex == 1) Color.White else Color(0xFF6B7280)
                    ),
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FavoriteKosItem(
    favoriteEntry: FavoriteEntry,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val kos = favoriteEntry.kos
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val formattedPrice = priceFormatter.format(kos.pricePerMonth).replace(",00", "")
    val location = "${kos.address}, ${kos.city}"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Kos Image with actual image from database
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Try to use thumbnailUrl or first image from images list
                val imageUrl = kos.thumbnailUrl ?: kos.images.firstOrNull()
                
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = kos.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback gradient with icon
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(listOf(Color(0xFF5876FF), Color(0xFF8BA4FF)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🏠",
                            fontSize = 32.sp
                        )
                    }
                }
                
                // Favorite badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorited",
                        tint = Color.Red,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            
            // Kos Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", kos.rating),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1B2633)
                        )
                    )
                }
                
                // Kos Name
                Text(
                    text = kos.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2633)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF9E9E9E)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Price
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5876FF),
                        fontSize = 16.sp
                    )
                )
                
                // Date and Guest Info (similar to booking style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Date Added",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ditambahkan ${favoriteEntry.dateAdded}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF9E9E9E)
                            )
                        )
                    }
                }
                
                // Facilities preview
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Type",
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (kos.type.name) {
                            "PUTRA" -> "Kos Putra"
                            "PUTRI" -> "Kos Putri"
                            "CAMPUR" -> "Kos Campur"
                            else -> "Kos"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF9E9E9E)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingHistoryItem(
    booking: Booking,
    modifier: Modifier = Modifier
) {
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val formattedPrice = priceFormatter.format(booking.totalPrice).replace(",00", "")
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Kos Image
            Box(
                modifier = Modifier
                    .size(100.dp)
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
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF5876FF), Color(0xFF8B9DFF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏠", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
            
            // Booking Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Kos Name
                Text(
                    text = booking.kos?.name ?: "Kos #${booking.kosId}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2633)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Location
                if (booking.kos?.address != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = booking.kos.address,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6B7280)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // Booking Type & Room Quantity
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF0F4FF)
                    ) {
                        Text(
                            text = if (booking.bookingType == "yearly") "Tahunan" else "Bulanan",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF5876FF),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Text(
                            text = "${booking.roomQuantity} Kamar",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                
                // Price and Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedPrice,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5876FF)
                        )
                    )
                    
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (booking.status) {
                            "confirmed" -> Color(0xFFE8F5E9)
                            "pending" -> Color(0xFFFFF3E0)
                            "cancelled" -> Color(0xFFFFEBEE)
                            else -> Color(0xFFF5F5F5)
                        }
                    ) {
                        Text(
                            text = when (booking.status) {
                                "confirmed" -> "Dikonfirmasi"
                                "pending" -> "Menunggu"
                                "cancelled" -> "Dibatalkan"
                                else -> booking.status
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = when (booking.status) {
                                    "confirmed" -> Color(0xFF4CAF50)
                                    "pending" -> Color(0xFFFF9800)
                                    "cancelled" -> Color(0xFFF44336)
                                    else -> Color(0xFF6B7280)
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}
