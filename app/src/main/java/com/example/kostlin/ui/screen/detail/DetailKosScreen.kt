package com.example.kostlin.ui.screen.detail

import androidx.compose.foundation.Image
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

@Composable
fun DetailKosScreen(
    kosProperty: KosProperty,
    onBackClick: () -> Unit,
    onBookingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reviews = remember { KosDummyData.getReviewsByKosId(kosProperty.id) }
    val facilities = remember { KosDummyData.getKosFacilities(kosProperty.id) }
    val recommendedKos = remember { KosDummyData.getRecommendedKos(kosProperty.id) }
    
    var isFavorite by remember { mutableStateOf(KosDummyData.isFavorite(kosProperty.id)) }

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
                        isFavorite = if (isFavorite) {
                            KosDummyData.removeFromFavorite(kosProperty.id)
                            false
                        } else {
                            KosDummyData.addToFavorite(kosProperty)
                            true
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
                    
                    // Facilities
                    FacilitiesSection(facilities = facilities)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Description
                    DescriptionSection(kosProperty = kosProperty)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Location
                    LocationSection(kosProperty = kosProperty)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Reviews
                    ReviewsSection(reviews = reviews)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Recommended Kos
                    if (recommendedKos.isNotEmpty()) {
                        RecommendedKosSection(recommendedKos = recommendedKos)
                    }
                    
                    Spacer(modifier = Modifier.height(100.dp)) // Space for bottom button
                }
            }
        }
        
        // Bottom Price and Booking Button
        BottomBookingSection(
            kosProperty = kosProperty,
            onBookingClick = onBookingClick,
            modifier = Modifier.align(Alignment.BottomCenter)
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
        // Background Image (placeholder with gradient)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF6B8E23), Color(0xFF8FBC8F))
                    )
                )
        )
        
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
        
        // Title overlay
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Detail",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
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

@Composable
private fun FacilitiesSection(facilities: List<KosFacility>) {
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
                modifier = Modifier.clickable { /* TODO: Show all facilities */ }
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
                text = "Maps",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5876FF),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable { /* TODO: Open maps */ }
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Map placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Color(0xFFF5F5F5),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🗺️ Map Preview\n${kosProperty.location}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun ReviewsSection(reviews: List<KosReview>) {
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
                modifier = Modifier.clickable { /* TODO: Show all reviews */ }
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
