package com.example.kostlin.ui.screen.favorite

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kostlin.data.model.FavoriteKos
import com.example.kostlin.data.model.KosDummyData
import com.example.kostlin.data.model.KosProperty
import com.example.kostlin.ui.components.BottomNavigation
import com.example.kostlin.ui.components.BottomNavRoute

@Composable
fun FavoriteKosScreen(
    onBackClick: () -> Unit,
    onKosClick: (KosProperty) -> Unit,
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    val favoriteKosList = remember { KosDummyData.getFavoriteKosList() }
    val favoriteKosHistory = remember { KosDummyData.getFavoriteKosHistory() }
    
    val filteredFavorites = remember(searchQuery, selectedTabIndex) {
        val list = if (selectedTabIndex == 0) favoriteKosList else favoriteKosHistory
        if (searchQuery.isBlank()) {
            list
        } else {
            list.filter { 
                it.kosProperty.name.contains(searchQuery, ignoreCase = true) ||
                it.kosProperty.location.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation(
                currentRoute = BottomNavRoute.FAVORITE.route,
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
        FavoriteHeader(
            onBackClick = onBackClick
        )
        
        // Search Bar
        SearchSection(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        )
        
        // Tab Section
        TabSection(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
            favoriteCount = favoriteKosList.size,
            historyCount = favoriteKosHistory.size
        )
        
        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredFavorites) { favoriteKos ->
                FavoriteKosItem(
                    favoriteKos = favoriteKos,
                    onClick = { onKosClick(favoriteKos.kosProperty) }
                )
            }
            
            // Add some bottom padding
            item {
                Spacer(modifier = Modifier.height(16.dp))
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
    favoriteKos: FavoriteKos,
    onClick: () -> Unit
) {
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
            // Kos Image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = when (favoriteKos.kosProperty.id % 3) {
                            0 -> Brush.linearGradient(listOf(Color(0xFF6B8E23), Color(0xFF8FBC8F)))
                            1 -> Brush.linearGradient(listOf(Color(0xFF4682B4), Color(0xFF87CEEB)))
                            else -> Brush.linearGradient(listOf(Color(0xFFCD853F), Color(0xFFDEB887)))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for kos image
                Text(
                    text = "🏠",
                    fontSize = 32.sp
                )
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
                        text = favoriteKos.kosProperty.rating,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1B2633)
                        )
                    )
                }
                
                // Kos Name
                Text(
                    text = favoriteKos.kosProperty.name,
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
                        text = favoriteKos.kosProperty.location,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF9E9E9E)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Price
                Text(
                    text = favoriteKos.kosProperty.price,
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
                            text = "Ditambahkan ${favoriteKos.dateAdded}",
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
                        text = when (favoriteKos.kosProperty.type.name) {
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
