package com.example.kostlin.ui.screen.home

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kostlin.data.User


private data class Property(
    val title: String,
    val location: String,
    val price: String,
    val rating: String
)

private data class Category(
    val label: String
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    user: User?,
    onLogout: () -> Unit
) {
    val displayName = user?.fullName?.ifBlank { null } ?: "Mr. Jiharmok"

    val popularProperties = listOf(
        Property("Kost Putri", "Pauh, Padang", "Rp. 800.000 /Bulan", "4.5"),
        Property("Kost Utama", "Pauh, Padang", "Rp. 750.000 /Bulan", "4.4"),
        Property("Kost Mawar", "Padang Selatan", "Rp. 680.000 /Bulan", "4.6")
    )

    val categories = listOf(
        Category("All"),
        Category("Kost"),
        Category("Kontrakan"),
        Category("Apartment")
    )

    val recommendations = listOf(
        Property("Kost Pria", "Padang", "Rp. 590.000 /Bulan", "4.5"),
        Property("Kost Elite", "Padang Utara", "Rp. 950.000 /Bulan", "4.8")
    )

    Scaffold(
        modifier = modifier.background(Color(0xFFF7F9FF)),
        bottomBar = {
            HomeBottomNavigation()
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                HeaderSection(
                    name = displayName,
                    onLogout = onLogout
                )
            }

            item {
                LocationBanner()
            }

            item {
                SectionTitle(
                    title = "Paling Populer",
                    actionLabel = "Lihat Semua"
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(popularProperties) { property ->
                        PopularPropertyCard(property)
                    }
                }
            }

            item {
                SectionTitle(
                    title = "Rekomendasi Untuk Anda",
                    actionLabel = "Lihat Semua"
                )
            }

            item {
                CategoryRow(categories = categories)
            }

            items(recommendations) { item ->
                RecommendationCard(property = item)
            }
        }
    }
}

@Composable
private fun HeaderSection(
    name: String,
    onLogout: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    color = Color.LightGray
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        tint = Color(0xFF4B5C6B)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2633)
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Location",
                            tint = Color(0xFF4B5C6B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Padang",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF4B5C6B)
                            )
                        )
                    }
                }
            }

            Row {
                IconButton(onClick = { /* TODO: search */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF1B2633)
                    )
                }
                IconButton(onClick = { /* TODO: notifications */ }) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notifications",
                        tint = Color(0xFF1B2633)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Keluar",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF5876FF),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.clickable { onLogout() }
        )
    }
}

@Composable
private fun LocationBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF4FF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = Color(0xFF5876FF)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Ganti lokasi untuk melihat kos terdekat",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF1B2633),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Change location",
                tint = Color(0xFF5876FF)
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    actionLabel: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2633)
            )
        )
        Text(
            text = actionLabel,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF5876FF),
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun PopularPropertyCard(
    property: Property
) {
    Card(
        modifier = Modifier
            .size(width = 200.dp, height = 220.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFF8DC26F), Color(0xFFF0C27B))
                            )
                        )
                )
                IconButton(
                    onClick = { /* TODO favourite */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(32.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.85f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favourite",
                        tint = Color(0xFFE84362)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = property.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = property.location,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = property.price,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2633)
                    )
                )
                Text(
                    text = "⭐ ${property.rating}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF4B5C6B)
                    )
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(
    categories: List<Category>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.forEachIndexed { index, category ->
            val isSelected = index == 0
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp)),
                color = if (isSelected) Color(0xFF5876FF) else Color.White,
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {
                Text(
                    text = category.label,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color.White else Color(0xFF1B2633)
                    )
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    property: Property
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFF2994A), Color(0xFFF2C94C))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberVectorPainter(Icons.Default.Home),
                    contentDescription = property.title,
                    modifier = Modifier.size(32.dp),
                    alpha = 0.85f
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2633)
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = property.location,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF4B5C6B)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = property.location,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF4B5C6B)
                        )
                    )
                }
                Text(
                    text = property.price,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5876FF),
                        fontSize = 16.sp
                    )
                )
            }
            Text(
                text = "⭐ ${property.rating}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF2C94C)
                )
            )
        }
    }
}

@Composable
private fun HomeBottomNavigation() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Icons.Default.Home to "Home",
            Icons.Default.BookmarkBorder to "My Booking",
            Icons.Default.ChatBubbleOutline to "Message",
            Icons.Default.Person to "Profile"
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == 0,
                onClick = { /* TODO: bottom navigation action */ },
                icon = {
                    Icon(
                        imageVector = item.first,
                        contentDescription = item.second,
                        tint = if (index == 0) Color(0xFF5876FF) else Color(0xFF4B5C6B)
                    )
                },
                label = {
                    Text(
                        text = item.second,
                        color = if (index == 0) Color(0xFF5876FF) else Color(0xFF4B5C6B)
                    )
                }
            )
        }
    }
}

