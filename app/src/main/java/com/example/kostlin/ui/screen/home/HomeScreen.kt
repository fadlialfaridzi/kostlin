package com.example.kostlin.ui.screen.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kostlin.core.di.AppContainer
import com.example.kostlin.data.model.KosProperty
import com.example.kostlin.domain.model.KosCategory
import com.example.kostlin.ui.components.BottomNavRoute
import com.example.kostlin.ui.components.BottomNavigation
import com.example.kostlin.ui.model.toKosProperty
import com.example.kostlin.ui.screen.detail.DetailKosScreen
import com.example.kostlin.ui.screen.favorite.FavoriteKosScreen
import com.example.kostlin.ui.screen.favorite.FavoriteViewModel
import com.example.kostlin.ui.screen.favorite.FavoriteViewModelFactory
import com.example.kostlin.ui.screen.add.AddKosScreen
import com.example.kostlin.ui.screen.add.AddKosViewModel
import com.example.kostlin.ui.screen.add.AddKosViewModelFactory
import com.example.kostlin.ui.screen.allkos.AllKosScreen
import com.example.kostlin.ui.screen.profile.ProfileScreen
import com.example.kostlin.ui.screen.profile.ProfileViewModel
import com.example.kostlin.ui.screen.profile.ProfileViewModelFactory
import com.example.kostlin.ui.screen.search.SearchScreen
import com.example.kostlin.ui.screen.map.MapKosScreen
import com.example.kostlin.ui.screen.booking.BookingViewModel
import com.example.kostlin.ui.screen.booking.BookingViewModelFactory
import com.example.kostlin.ui.screen.edit.EditKosScreen
import com.example.kostlin.ui.screen.edit.EditKosViewModel
import com.example.kostlin.ui.screen.edit.EditKosViewModelFactory
import com.example.kostlin.core.network.NetworkClient

private data class Category(
    val label: String,
    val type: KosCategory? = null
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String,
    onLogout: () -> Unit,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    val displayName = userName.ifBlank { "Mr. Jiharmok" }
    
    // Shared FavoriteViewModel for DetailKosScreen and FavoriteKosScreen
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(AppContainer.favoriteRepository)
    )
    
    // Shared BookingViewModel for booking history
    val bookingViewModel: BookingViewModel = viewModel(
        factory = BookingViewModelFactory(AppContainer.bookingRepository)
    )
    
    // ProfileViewModel for profile page
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(NetworkClient.apiService)
    )
    
    // EditKosViewModel for editing kos
    val editKosViewModel: EditKosViewModel = viewModel(
        factory = EditKosViewModelFactory(NetworkClient.apiService)
    )
    
    var showSearchScreen by remember { mutableStateOf(false) }
    var showMapScreen by remember { mutableStateOf(false) }
    var selectedKosProperty by remember { mutableStateOf<KosProperty?>(null) }
    var showFavoriteScreen by remember { mutableStateOf(false) }
    var showAddKosScreen by remember { mutableStateOf(false) }
    var showAllKosScreen by remember { mutableStateOf(false) }
    var showProfileScreen by remember { mutableStateOf(false) }
    var selectedKosToEdit by remember { mutableStateOf<com.example.kostlin.domain.model.Kos?>(null) }
    val uiState by homeViewModel.uiState.collectAsState()
    
    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        homeViewModel.onLocationPermissionResult(fineLocationGranted || coarseLocationGranted)
    }
    
    // Initialize location and request permission on first composition
    LaunchedEffect(Unit) {
        homeViewModel.initLocation(context)
        if (!homeViewModel.hasLocationPermission()) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            homeViewModel.fetchUserLocation()
        }
    }

    val categories = listOf(
        Category("All"),
        Category("Kos Putra", KosCategory.PUTRA),
        Category("Kos Putri", KosCategory.PUTRI),
        Category("Kos Campur", KosCategory.CAMPUR)
    )
    val selectedCategoryIndex = remember(uiState.selectedCategory) {
        categories.indexOfFirst { it.type == uiState.selectedCategory }.takeIf { it >= 0 } ?: 0
    }

    // Screen visibility states for animations
    val showHomeContent = !showAllKosScreen && selectedKosProperty == null && !showAddKosScreen && 
                          !showFavoriteScreen && !showSearchScreen && !showProfileScreen && !showMapScreen
    
    // Determine current route for bottom navigation
    val currentRoute = when {
        showFavoriteScreen -> BottomNavRoute.FAVORITE.route
        showProfileScreen -> BottomNavRoute.PROFILE.route
        showAddKosScreen -> BottomNavRoute.ADD.route
        else -> BottomNavRoute.HOME.route
    }
    
    // Show bottom navigation only on main screens (not on detail, search, map, edit, etc.)
    val showBottomNav = !showAllKosScreen && selectedKosProperty == null && 
                        !showSearchScreen && !showMapScreen && selectedKosToEdit == null
    
    Scaffold(
        modifier = modifier.background(Color(0xFFF7F9FF)),
        bottomBar = {
            if (showBottomNav) {
                BottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        when (route) {
                            BottomNavRoute.HOME.route -> {
                                showFavoriteScreen = false
                                showProfileScreen = false
                                showAddKosScreen = false
                            }
                            BottomNavRoute.FAVORITE.route -> {
                                showFavoriteScreen = true
                                showProfileScreen = false
                                showAddKosScreen = false
                            }
                            BottomNavRoute.ADD.route -> {
                                showAddKosScreen = true
                                showFavoriteScreen = false
                                showProfileScreen = false
                            }
                            BottomNavRoute.PROFILE.route -> {
                                showProfileScreen = true
                                showFavoriteScreen = false
                                showAddKosScreen = false
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Home Screen (base layer)
            AnimatedVisibility(
                visible = showHomeContent,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        HeaderSection(
                            name = displayName,
                            cityName = uiState.userCity,
                            isLocationLoading = uiState.isLocationLoading,
                            onLogout = onLogout,
                            onSearchClick = { showSearchScreen = true }
                        )
                    }

                    item {
                        LocationBanner(
                            onChangeLocation = { showMapScreen = true }
                        )
                    }

                    item {
                        SectionTitle(
                            title = "Paling Populer",
                            actionLabel = "Lihat Semua",
                        )
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.popularKos) { property ->
                                PopularPropertyCard(
                                    property = property,
                                    onClick = { selectedKosProperty = property }
                                )
                            }
                        }
                    }

                    item {
                        SectionTitle(
                            title = "Semua Kos",
                            actionLabel = if (uiState.recommendations.size > 5) "Lihat Semua" else "",
                            onActionClick = { showAllKosScreen = true }
                        )
                    }

                    // Show only first 5 recommendations vertically
                    items(uiState.recommendations.take(5)) { item ->
                        RecommendationCard(
                            property = item,
                            onClick = { selectedKosProperty = item }
                        )
                    }
                    
                    // Show "Lihat Semua" button if more than 5 items
                    if (uiState.recommendations.size > 5) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(onClick = { showAllKosScreen = true }) {
                                    Text(
                                        text = "Lihat ${uiState.recommendations.size - 5} kos lainnya →",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF5876FF),
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                    
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔄 Tap untuk refresh",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF5876FF),
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clickable { homeViewModel.refreshHome(forcePopular = true) }
                            )
                        }
                    }
                }
            }
            
            // All Kos Screen
            AnimatedVisibility(
                visible = showAllKosScreen,
                enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300)),
                exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(tween(300))
            ) {
                AllKosScreen(
                    onBackClick = { showAllKosScreen = false },
                    kosList = uiState.recommendations,
                    onKosClick = { kosProperty ->
                        selectedKosProperty = kosProperty
                        showAllKosScreen = false
                    }
                )
            }
            
            // Detail Kos Screen
            AnimatedVisibility(
                visible = selectedKosProperty != null,
                enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300)),
                exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(tween(300))
            ) {
                selectedKosProperty?.let { property ->
                    DetailKosScreen(
                        kosProperty = property,
                        onBackClick = { selectedKosProperty = null },
                        onBookingClick = { /* Navigate back to home after booking */ },
                        favoriteViewModel = favoriteViewModel,
                        bookingViewModel = bookingViewModel,
                        modifier = Modifier
                    )
                }
            }
            
            // Add Kos Screen with inner Scaffold for its own top bar
            AnimatedVisibility(
                visible = showAddKosScreen,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) + fadeIn(tween(300)),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)) + fadeOut(tween(300))
            ) {
                val addKosViewModel: AddKosViewModel = viewModel(
                    factory = AddKosViewModelFactory(AppContainer.kosRepository)
                )
                AddKosScreen(
                    onBackClick = { 
                        showAddKosScreen = false
                        homeViewModel.refreshHome(forcePopular = true)
                    },
                    addKosViewModel = addKosViewModel,
                    modifier = Modifier
                )
            }
            
            // Favorite Screen
            AnimatedVisibility(
                visible = showFavoriteScreen,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                FavoriteKosScreen(
                    onBackClick = { showFavoriteScreen = false },
                    onKosClick = { kos -> 
                        selectedKosProperty = kos.toKosProperty()
                        showFavoriteScreen = false  // Close favorite screen to show detail
                    },
                    favoriteViewModel = favoriteViewModel,
                    bookingViewModel = bookingViewModel,
                    modifier = Modifier
                )
            }
            
            // Search Screen
            AnimatedVisibility(
                visible = showSearchScreen,
                enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(300)) + fadeIn(tween(300)),
                exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(300)) + fadeOut(tween(300))
            ) {
                SearchScreen(
                    onBackClick = { showSearchScreen = false },
                    onKosClick = { kos ->
                        selectedKosProperty = kos.toKosProperty()
                        showSearchScreen = false
                    },
                    modifier = Modifier
                )
            }
            
            // Map Kos Screen
            AnimatedVisibility(
                visible = showMapScreen,
                enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300)),
                exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(tween(300))
            ) {
                MapKosScreen(
                    onBackClick = { showMapScreen = false },
                    onKosClick = { kosProperty ->
                        selectedKosProperty = kosProperty
                        showMapScreen = false
                    }
                )
            }
            
            // Profile Screen
            AnimatedVisibility(
                visible = showProfileScreen && selectedKosToEdit == null,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                ProfileScreen(
                    userName = displayName,
                    userEmail = "jiharmok@example.com",
                    onBackClick = { showProfileScreen = false },
                    onChangePasswordClick = { /* TODO: Handle change password */ },
                    onLogout = onLogout,
                    profileViewModel = profileViewModel,
                    onEditKos = { kos ->
                        selectedKosToEdit = kos
                        editKosViewModel.resetState()
                    }
                )
            }
            
            // Edit Kos Screen
            AnimatedVisibility(
                visible = selectedKosToEdit != null,
                enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300)),
                exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(tween(300))
            ) {
                selectedKosToEdit?.let { kos ->
                    EditKosScreen(
                        kos = kos,
                        onBackClick = { selectedKosToEdit = null },
                        onSaveSuccess = { 
                            selectedKosToEdit = null
                            profileViewModel.fetchMyKos()
                        },
                        editKosViewModel = editKosViewModel
                    )
                }
            }
            
            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5876FF))
                }
            }
            
            // Error message
            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { homeViewModel.refreshHome(forcePopular = true) }
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    name: String,
    cityName: String,
    isLocationLoading: Boolean,
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
                        if (isLocationLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF5876FF)
                            )
                        } else {
                            Text(
                                text = cityName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF4B5C6B)
                                )
                            )
                        }
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
    }
}

@Composable
private fun LocationBanner(
    onChangeLocation: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChangeLocation() },
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
                        text = "Cari kos di peta",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF1B2633),
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "Lihat semua kos dengan lokasi",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
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
    actionLabel: String,
    onActionClick: () -> Unit = {}
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
            ),
            modifier = Modifier.clickable { onActionClick() }
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
                // Display actual image or fallback gradient
                if (property.imageUrl != null) {
                    AsyncImage(
                        model = property.imageUrl,
                        contentDescription = property.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(Color(0xFF6B8E23), Color(0xFF8FBC8F))
                                )
                            )
                    )
                }
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
            // Image box with AsyncImage or fallback
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (property.imageUrl != null) {
                    AsyncImage(
                        model = property.imageUrl,
                        contentDescription = property.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
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
                }
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
