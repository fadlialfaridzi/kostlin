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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kostlin.ui.screen.search.SearchScreen
import com.example.kostlin.ui.components.BottomNavigation
import com.example.kostlin.ui.components.BottomNavRoute
import com.example.kostlin.data.model.KosProperty
import com.example.kostlin.data.model.KosDummyData
import com.example.kostlin.data.model.KosType
import com.example.kostlin.ui.screen.detail.DetailKosScreen
import com.example.kostlin.ui.screen.favorite.FavoriteKosScreen
import com.example.kostlin.ui.screen.add.AddKosScreen
private data class Category(
    val label: String,
    val type: KosType? = null
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String,
    onLogout: () -> Unit
) {
    val displayName = userName.ifBlank { "Mr. Jiharmok" }
    var showSearchScreen by remember { mutableStateOf(false) }
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var selectedKosProperty by remember { mutableStateOf<KosProperty?>(null) }
    var showFavoriteScreen by remember { mutableStateOf(false) }
    var showAddKosScreen by remember { mutableStateOf(false) }

    val popularProperties = KosDummyData.getPopularProperties()

    val categories = listOf(
        Category("All"),
        Category("Kos Putra", KosType.PUTRA),
        Category("Kos Putri", KosType.PUTRI),
        Category("Kos Campur", KosType.CAMPUR)
    )

    val recommendations = remember(selectedCategoryIndex) {
        val selectedCategory = categories[selectedCategoryIndex]
        when {
            selectedCategory.type == null -> KosDummyData.allKosProperties // All
            selectedCategory.type == KosType.PUTRA -> KosDummyData.getPropertiesByType(KosType.PUTRA)
            selectedCategory.type == KosType.PUTRI -> KosDummyData.getPropertiesByType(KosType.PUTRI)
            selectedCategory.type == KosType.CAMPUR -> KosDummyData.getPropertiesByType(KosType.CAMPUR)
            else -> KosDummyData.getRecommendedProperties()
        }
    }

    when {
        selectedKosProperty != null -> {
            DetailKosScreen(
                kosProperty = selectedKosProperty!!,
                onBackClick = { selectedKosProperty = null },
                onBookingClick = { /* TODO: Handle booking */ },
                modifier = modifier
            )
        }
        showAddKosScreen -> {
            AddKosScreen(
                onBackClick = { showAddKosScreen = false },
                onNavigate = { route ->
                    when (route) {
                        BottomNavRoute.HOME.route -> showAddKosScreen = false
                        BottomNavRoute.FAVORITE.route -> {
                            showAddKosScreen = false
                            showFavoriteScreen = true
                        }
                        // Handle other routes
                    }
                },
                modifier = modifier
            )
        }
        showFavoriteScreen -> {
            FavoriteKosScreen(
                onBackClick = { showFavoriteScreen = false },
                onKosClick = { kosProperty -> selectedKosProperty = kosProperty },
                onNavigate = { route ->
                    when (route) {
                        BottomNavRoute.HOME.route -> showFavoriteScreen = false
                        BottomNavRoute.ADD.route -> {
                            showFavoriteScreen = false
                            showAddKosScreen = true
                        }
                        // Handle other routes
                    }
                },
                modifier = modifier
            )
        }
        showSearchScreen -> {
            SearchScreen(
                onBackClick = { showSearchScreen = false },
                modifier = modifier
            )
        }
        else -> {
        Scaffold(
        modifier = modifier.background(Color(0xFFF7F9FF)),
        bottomBar = {
            BottomNavigation(
                currentRoute = BottomNavRoute.HOME.route,
                onNavigate = { route ->
                    when (route) {
                        BottomNavRoute.FAVORITE.route -> showFavoriteScreen = true
                        BottomNavRoute.ADD.route -> showAddKosScreen = true
                        // TODO: Handle other navigation routes
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                HeaderSection(
                    name = displayName,
                    onLogout = onLogout,
                    onSearchClick = { showSearchScreen = true }
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
                        PopularPropertyCard(
                            property = property,
                            onClick = { selectedKosProperty = property }
                        )
                    }
                }
            }

            item {
                val sectionTitle = when (selectedCategoryIndex) {
                    0 -> "Semua Kos"
                    1 -> "Kos Putra"
                    2 -> "Kos Putri"
                    3 -> "Kos Campur"
                    else -> "Rekomendasi Untuk Anda"
                }
                SectionTitle(
                    title = sectionTitle,
                    actionLabel = "Lihat Semua"
                )
            }

            item {
                CategoryRow(
                    categories = categories,
                    selectedIndex = selectedCategoryIndex,
                    onCategoryClick = { index ->
                        selectedCategoryIndex = index
                    }
                )
            }

            items(recommendations) { item ->
                RecommendationCard(
                    property = item,
                    onClick = { selectedKosProperty = item }
                )
            }
        }
    }
        }
    }
}

@Composable
private fun HeaderSection(
    name: String,
    onLogout: () -> Unit,
    onSearchClick: () -> Unit
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
                IconButton(onClick = onSearchClick) {
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
    property: KosProperty,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .size(width = 200.dp, height = 220.dp)
            .clickable { onClick() },
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
                                listOf(Color(0xFF6B8E23), Color(0xFF8FBC8F))
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
                            text = property.name,
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
    categories: List<Category>,
    selectedIndex: Int = 0,
    onCategoryClick: (Int) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.forEachIndexed { index, category ->
            val isSelected = index == selectedIndex
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onCategoryClick(index) },
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
    property: KosProperty,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    contentDescription = property.name,
                    modifier = Modifier.size(32.dp),
                    alpha = 0.85f
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = property.name,
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



