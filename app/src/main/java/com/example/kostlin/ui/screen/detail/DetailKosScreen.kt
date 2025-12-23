package com.example.kostlin.ui.screen.detail

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import coil.compose.AsyncImage
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kostlin.R
import com.example.kostlin.data.model.KosProperty
import com.example.kostlin.data.model.KosDummyData
import com.example.kostlin.data.model.KosReview
import com.example.kostlin.data.model.KosFacility
import com.example.kostlin.ui.screen.booking.BookingRequestScreen
import com.example.kostlin.ui.screen.booking.BookingConfirmationScreen
import com.example.kostlin.ui.screen.booking.BookingLoadingScreen
import com.example.kostlin.ui.screen.booking.BookingSuccessScreen
import com.example.kostlin.ui.screen.booking.BookingType
import com.example.kostlin.ui.screen.booking.BookingDetail
import com.example.kostlin.ui.screen.booking.BookingViewModel
import com.example.kostlin.ui.screen.favorite.FavoriteViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.kostlin.core.session.SessionManager

enum class BookingStep {
    REQUEST, CONFIRMATION, LOADING, SUCCESS
}

@Composable
fun DetailKosScreen(
    kosProperty: KosProperty,
    onBackClick: () -> Unit,
    onBookingClick: () -> Unit,
    favoriteViewModel: FavoriteViewModel,
    bookingViewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val reviews = remember { KosDummyData.getReviewsByKosId(kosProperty.id) }
    // Use facilities from kosProperty directly (List<String>)
    val facilitiesList = kosProperty.facilities
    
    // Get favorite state from ViewModel
    val favoriteState by favoriteViewModel.uiState.collectAsState()
    // Derive isFavorite from current favorites list - compare as strings since Kos.id is String
    val isFavorite = favoriteState.favorites.any { 
        it.kos.id == kosProperty.id.toString() 
    }
    var showAllFacilities by remember { mutableStateOf(false) }
    
    // Booking flow states
    var bookingStep by remember { mutableStateOf<BookingStep?>(null) }
    var selectedBookingType by remember { mutableStateOf(BookingType.MONTHLY) }
    var selectedRoomQuantity by remember { mutableIntStateOf(1) }
    var bookingDetail by remember { mutableStateOf<BookingDetail?>(null) }
    
    // Snackbar for error messages
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    // Get current user ID from SessionManager
    val currentUserId = SessionManager.userId

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Image
            item {
                KosImageHeader(
                    kosProperty = kosProperty,
                    onBackClick = onBackClick,
                    isFavorite = isFavorite,
                    onFavoriteClick = {
                        // Debug log to see what's happening
                        android.util.Log.d("FavoriteDebug", "kosProperty.id: ${kosProperty.id}")
                        android.util.Log.d("FavoriteDebug", "isFavorite: $isFavorite")
                        android.util.Log.d("FavoriteDebug", "favorites: ${favoriteState.favorites.map { it.kos.id }}")
                        
                        if (isFavorite) {
                            android.util.Log.d("FavoriteDebug", "Calling removeFavorite with id: ${kosProperty.id}")
                            favoriteViewModel.removeFavorite(kosProperty.id)
                            android.util.Log.d("FavoriteDebug", "removeFavorite called!")
                        } else {
                            android.util.Log.d("FavoriteDebug", "Calling addFavorite with id: ${kosProperty.id}")
                            favoriteViewModel.addFavorite(kosProperty.id)
                            android.util.Log.d("FavoriteDebug", "addFavorite called!")
                        }
                    }
                )
            }
            
            // Content
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    // Title and Rating
                    KosTitleSection(kosProperty = kosProperty)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Facilities (using string list from database)
                    SimpleFacilitiesSection(facilities = facilitiesList)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Description
                    DescriptionSection(kosProperty = kosProperty)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Location with Map Preview
                    LocationSection(kosProperty = kosProperty)
                    
                    Spacer(modifier = Modifier.height(100.dp)) // Space for bottom button
                }
            }
        }


        // Booking Flow
        when (bookingStep) {
            BookingStep.REQUEST -> {
                BookingRequestScreen(
                    kosProperty = kosProperty,
                    onBackClick = { bookingStep = null },
                    onContinueClick = { type, quantity ->
                        selectedBookingType = type
                        selectedRoomQuantity = quantity
                        bookingDetail = BookingDetail.fromKosProperty(
                            kosProperty = kosProperty,
                            bookingType = type,
                            roomQuantity = quantity
                        )
                        bookingStep = BookingStep.CONFIRMATION
                    }
                )
            }

            BookingStep.CONFIRMATION -> {
                bookingDetail?.let { detail ->
                    BookingConfirmationScreen(
                        bookingDetail = detail,
                        onBackClick = { bookingStep = BookingStep.REQUEST },
                        onConfirmClick = {
                            // Call API to create booking
                            bookingStep = BookingStep.LOADING
                            bookingViewModel.createBooking(
                                kosId = detail.kosId,
                                bookingType = detail.bookingType,
                                roomQuantity = detail.roomQuantity,
                                totalPrice = detail.totalPrice,
                                onSuccess = {
                                    bookingStep = BookingStep.SUCCESS
                                }
                            )
                        }
                    )
                }
            }

            BookingStep.LOADING -> {
                BookingLoadingScreen(
                    onComplete = { 
                        // Loading will complete when API returns success via callback
                        // Do nothing here - success is triggered by createBooking callback
                    }
                )
            }

            BookingStep.SUCCESS -> {
                BookingSuccessScreen(
                    onContinue = {
                        bookingStep = null
                        onBookingClick()
                    }
                )
            }

            null -> {
                // Bottom Price and Booking Button
                BottomBookingSection(
                    kosProperty = kosProperty,
                    onBookingClick = { 
                        // Check if current user is the owner
                        if (currentUserId != null && kosProperty.ownerId != null && currentUserId == kosProperty.ownerId) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Tidak dapat booking kos sendiri")
                            }
                        } else {
                            bookingStep = BookingStep.REQUEST
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
        
        // Snackbar Host for error messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )
    }
}

@Composable
private fun KosImageHeader(
    kosProperty: KosProperty,
    onBackClick: () -> Unit,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Background Image from database or gradient fallback
        if (kosProperty.imageUrl != null) {
            AsyncImage(
                model = kosProperty.imageUrl,
                contentDescription = kosProperty.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF5876FF), Color(0xFF8BA4FF))
                        )
                    )
            )
        }
        
        // Header Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(
                        Color.White.copy(alpha = 0.9f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            
            Row {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = { /* TODO: More options */ },
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = Color.Black
                    )
                }
            }
        }
        
    }
}

@Composable
private fun KosTitleSection(kosProperty: KosProperty) {
    Column {
        Text(
            text = kosProperty.name,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2633)
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = Color(0xFF5876FF),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = kosProperty.location,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF6B7280)
                )
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = kosProperty.rating,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1B2633)
                )
            )
        }
    }
}

// Simple facilities section that takes List<String> directly
@Composable
private fun SimpleFacilitiesSection(facilities: List<String>) {
    Column {
        Text(
            text = "Fasilitas",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2633)
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (facilities.isEmpty()) {
            Text(
                text = "Belum ada fasilitas terdaftar",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF6B7280)
                )
            )
        } else {
            // Display facilities in a flow row style
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(facilities.size) { index ->
                    val facilityName = facilities[index]
                    val icon = getFacilityIcon(facilityName)
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0F4FF)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = facilityName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF1B2633),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to get emoji icon for facility
private fun getFacilityIcon(facilityName: String): String {
    return when (facilityName.lowercase()) {
        "wifi" -> "📶"
        "ac" -> "❄️"
        "kamar mandi dalam" -> "🚿"
        "dapur bersama" -> "🍳"
        "parkir motor" -> "🏍️"
        "security 24 jam" -> "🛡️"
        "laundry" -> "👕"
        "lemari" -> "🗄️"
        else -> "✨"
    }
}

@Composable
private fun FacilitiesSection(
    facilities: List<KosFacility>,
    onShowAll: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fasilitas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2633)
                )
            )
            
            Text(
                text = "Semua",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5876FF),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable { onShowAll() }
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(facilities.take(4)) { facility ->
                FacilityItem(facility = facility)
            }
        }
    }
}

@Composable
private fun FacilityItem(facility: KosFacility) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF0F4FF)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Placeholder icon based on facility type
                val iconResource = when (facility.icon) {
                    "wifi" -> "📶"
                    "ac" -> "❄️"
                    "bathroom" -> "🚿"
                    "kitchen" -> "🍳"
                    "parking" -> "🏍️"
                    "security" -> "🛡️"
                    "laundry" -> "👕"
                    "study" -> "📚"
                    "gym" -> "💪"
                    "rooftop" -> "🏠"
                    "balcony" -> "🌅"
                    "private_kitchen" -> "🍽️"
                    else -> "✨"
                }
                
                Text(
                    text = iconResource,
                    fontSize = 20.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = facility.name,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DescriptionSection(kosProperty: KosProperty) {
    Column {
        Text(
            text = "Deskripsi",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2633)
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = kosProperty.description,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF6B7280),
                lineHeight = 20.sp
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Read More",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF5876FF),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.clickable { /* TODO: Show full description */ }
        )
    }
}

@Composable
private fun LocationSection(kosProperty: KosProperty) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Default coordinates for Padang area (since KosProperty doesn't have lat/lng)
    val defaultLat = -0.9471
    val defaultLng = 100.4172
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lokasi",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2633)
                )
            )
            
            Text(
                text = kosProperty.location,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5876FF),
                    fontWeight = FontWeight.Medium
                )
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // OSM Map Preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    // Configure osmdroid
                    Configuration.getInstance().apply {
                        userAgentValue = ctx.packageName
                    }
                    
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(false) // Disable interaction for preview
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(defaultLat, defaultLng))
                        
                        // Add marker
                        val marker = Marker(this).apply {
                            position = GeoPoint(defaultLat, defaultLng)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = kosProperty.name
                        }
                        overlays.add(marker)
                    }
                }
            )
        }
    }
}

@Composable
private fun ReviewsSection(
    reviews: List<KosReview>,
    onShowAll: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ulasan (${reviews.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2633)
                )
            )
            
            Text(
                text = "Lihat Semua",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5876FF),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable { onShowAll() }
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        reviews.take(2).forEach { review ->
            ReviewItem(review = review)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ReviewItem(review: KosReview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User avatar placeholder
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color(0xFF5876FF)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.userName.first().toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = review.userName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1B2633)
                            )
                        )
                        
                        if (review.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF10B981)
                                )
                            )
                        }
                    }
                    
                    Text(
                        text = review.date,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6B7280)
                        )
                    )
                }
                
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = review.rating.toString(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1B2633)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF6B7280),
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@Composable
private fun RecommendedKosSection(recommendedKos: List<KosProperty>) {
    Column {
        Text(
            text = "Rekomendasi Lainnya",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2633)
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recommendedKos) { kos ->
                RecommendedKosItem(kos = kos)
            }
        }
    }
}

@Composable
private fun RecommendedKosItem(kos: KosProperty) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { /* TODO: Navigate to detail */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF6B8E23), Color(0xFF8FBC8F))
                        )
                    )
            )
            
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = kos.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1B2633)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = kos.location,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF6B7280)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = kos.price,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5876FF)
                        )
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = kos.rating,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF1B2633)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBookingSection(
    kosProperty: KosProperty,
    onBookingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Harga",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF6B7280)
                    )
                )
                Text(
                    text = kosProperty.price,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2633)
                    )
                )
            }
            
            Button(
                onClick = onBookingClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5876FF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(180.dp)
            ) {
                Text(
                    text = "Pesan Sekarang!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }
        }
    }
}
