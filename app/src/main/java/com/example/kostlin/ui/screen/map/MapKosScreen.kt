package com.example.kostlin.ui.screen.map

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.core.network.NetworkClient
import com.example.kostlin.core.network.executeRequest
import com.example.kostlin.data.mapper.toDomain
import com.example.kostlin.data.model.KosProperty
import com.example.kostlin.ui.model.toKosProperty
import com.example.kostlin.data.remote.service.ApiService
import com.example.kostlin.domain.model.Kos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.text.NumberFormat
import java.util.Locale

// ViewModel
data class MapKosUiState(
    val isLoading: Boolean = false,
    val kosList: List<Kos> = emptyList(),
    val selectedKos: Kos? = null,
    val error: String? = null
)

class MapKosViewModel(
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MapKosUiState())
    val uiState: StateFlow<MapKosUiState> = _uiState.asStateFlow()
    
    fun refreshKosList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = executeRequest(defaultValue = emptyList()) { 
                apiService.getKosList() 
            }) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            kosList = result.data.map { dto -> dto.toDomain() }
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }
    
    fun selectKos(kos: Kos?) {
        _uiState.update { it.copy(selectedKos = kos) }
    }
}

class MapKosViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapKosViewModel::class.java)) {
            return MapKosViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// Main Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapKosScreen(
    onBackClick: () -> Unit,
    onKosClick: (KosProperty) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: MapKosViewModel = viewModel(
        factory = MapKosViewModelFactory(NetworkClient.apiService)
    )
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var mapView by remember { mutableStateOf<MapView?>(null) }
    
    // Configure osmdroid and fetch kos list
    LaunchedEffect(Unit) {
        configureOsmdroid(context)
        viewModel.refreshKosList()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cari di Peta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Center to Indonesia
                    mapView?.controller?.animateTo(GeoPoint(-0.7893, 113.9213))
                    mapView?.controller?.setZoom(5.0)
                },
                containerColor = Color(0xFF5876FF)
            ) {
                Icon(Icons.Default.MyLocation, "Center Map", tint = Color.White)
            }
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Map View
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(12.0)
                        // Default center: Indonesia
                        controller.setCenter(GeoPoint(-0.7893, 113.9213))
                        mapView = this
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { map ->
                    // Clear existing markers
                    map.overlays.clear()
                    
                    // Add markers for each kos
                    uiState.kosList.forEach { kos ->
                        if (kos.latitude != null && kos.longitude != null) {
                            val marker = Marker(map).apply {
                                position = GeoPoint(kos.latitude, kos.longitude)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = kos.name
                                snippet = kos.city
                                setOnMarkerClickListener { _, _ ->
                                    viewModel.selectKos(kos)
                                    true
                                }
                            }
                            map.overlays.add(marker)
                        }
                    }
                    
                    // If there are kos with location, center on the first one
                    if (uiState.kosList.isNotEmpty() && mapView != null) {
                        val firstWithLocation = uiState.kosList.find { 
                            it.latitude != null && it.longitude != null 
                        }
                        firstWithLocation?.let {
                            map.controller.setCenter(GeoPoint(it.latitude!!, it.longitude!!))
                        }
                    }
                    
                    map.invalidate()
                }
            )
            
            // Dispose MapView
            DisposableEffect(Unit) {
                onDispose {
                    mapView?.onDetach()
                }
            }
            
            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5876FF))
                }
            }
            
            // Kos count badge
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF5876FF))
            ) {
                Text(
                    text = "${uiState.kosList.count { it.latitude != null }} kos di peta",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            
            // Selected Kos Card
            AnimatedVisibility(
                visible = uiState.selectedKos != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                uiState.selectedKos?.let { kos ->
                    KosInfoCard(
                        kos = kos,
                        onCardClick = { 
                            onKosClick(kos.toKosProperty())
                            viewModel.selectKos(null)
                        },
                        onDismiss = { viewModel.selectKos(null) },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun KosInfoCard(
    kos: Kos,
    onCardClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    if (kos.images.isNotEmpty()) {
                        AsyncImage(
                            model = kos.images.first(),
                            contentDescription = kos.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF5876FF), Color(0xFF8B9DFF))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏠", style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                }
                
                // Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = kos.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Place, 
                            "Location", 
                            Modifier.size(14.dp), 
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = kos.city,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                            maxLines = 1
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${priceFormatter.format(kos.pricePerMonth)}/bln",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5876FF)
                            )
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format("%.1f", kos.rating),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Tap untuk lihat detail →",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF5876FF),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

private fun configureOsmdroid(context: Context) {
    val config = Configuration.getInstance()
    config.userAgentValue = context.packageName
    
    val cacheDir = File(context.cacheDir, "osmdroid")
    if (!cacheDir.exists()) {
        cacheDir.mkdirs()
    }
    config.osmdroidTileCache = cacheDir
    config.osmdroidBasePath = cacheDir
}
