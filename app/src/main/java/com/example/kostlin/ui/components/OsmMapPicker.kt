package com.example.kostlin.ui.components

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File

/**
 * Optimized OpenStreetMap Location Picker Dialog
 * Features:
 * - Tile caching for faster subsequent loads
 * - Hardware acceleration
 * - Efficient memory management
 */
@Composable
fun OsmMapPickerDialog(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit,
    onRequestCurrentLocation: (() -> Unit)? = null
) {
    val context = LocalContext.current
    
    // Default to Padang, Indonesia if no initial location
    val defaultLat = -0.9471
    val defaultLng = 100.4172
    
    var selectedLatitude by remember { mutableDoubleStateOf(initialLatitude ?: defaultLat) }
    var selectedLongitude by remember { mutableDoubleStateOf(initialLongitude ?: defaultLng) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var marker by remember { mutableStateOf<Marker?>(null) }
    var isMapReady by remember { mutableStateOf(false) }
    
    // Configure osmdroid with optimizations
    LaunchedEffect(Unit) {
        configureOsmdroid(context)
    }
    
    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDetach()
            mapView = null
            marker = null
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF1B2633)
                            )
                        }
                        
                        Text(
                            text = "Pilih Lokasi Kos",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B2633)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )
                        
                        TextButton(
                            onClick = { onConfirm(selectedLatitude, selectedLongitude) }
                        ) {
                            Text(
                                text = "Pilih",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5876FF)
                                )
                            )
                        }
                    }
                }
                
                // Map Container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // OpenStreetMap View with optimizations
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            createOptimizedMapView(ctx).apply {
                                // Set initial position
                                controller.setZoom(16.0)
                                controller.setCenter(GeoPoint(selectedLatitude, selectedLongitude))
                                
                                // Add marker
                                val newMarker = Marker(this).apply {
                                    position = GeoPoint(selectedLatitude, selectedLongitude)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = "Lokasi Kos"
                                    isDraggable = true
                                    
                                    setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                                        override fun onMarkerDrag(m: Marker) {}
                                        
                                        override fun onMarkerDragEnd(m: Marker) {
                                            selectedLatitude = m.position.latitude
                                            selectedLongitude = m.position.longitude
                                        }
                                        
                                        override fun onMarkerDragStart(m: Marker) {}
                                    })
                                }
                                overlays.add(newMarker)
                                marker = newMarker
                                
                                // Add tap listener to move marker
                                val mapEventsReceiver = object : MapEventsReceiver {
                                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                        selectedLatitude = p.latitude
                                        selectedLongitude = p.longitude
                                        marker?.position = p
                                        invalidate()
                                        return true
                                    }
                                    
                                    override fun longPressHelper(p: GeoPoint): Boolean = false
                                }
                                overlays.add(0, MapEventsOverlay(mapEventsReceiver))
                                
                                mapView = this
                                isMapReady = true
                            }
                        },
                        update = { view ->
                            // Update marker position when state changes
                            marker?.position = GeoPoint(selectedLatitude, selectedLongitude)
                            view.invalidate()
                        }
                    )
                    
                    // Loading indicator while map initializes
                    if (!isMapReady) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Memuat peta...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF6B7280)
                                )
                            )
                        }
                    }
                    
                    // Zoom Controls
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(16.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column {
                                IconButton(
                                    onClick = { mapView?.controller?.zoomIn() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Zoom In",
                                        tint = Color(0xFF374151)
                                    )
                                }
                                IconButton(
                                    onClick = { mapView?.controller?.zoomOut() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Zoom Out",
                                        tint = Color(0xFF374151)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Current Location Button
                        if (onRequestCurrentLocation != null) {
                            Card(
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                IconButton(
                                    onClick = onRequestCurrentLocation
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = "Current Location",
                                        tint = Color(0xFF5876FF)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Bottom Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📍 Lokasi Terpilih",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B2633)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Latitude",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF6B7280)
                                    )
                                )
                                Text(
                                    text = String.format("%.6f", selectedLatitude),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1B2633)
                                    )
                                )
                            }
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Longitude",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF6B7280)
                                    )
                                )
                                Text(
                                    text = String.format("%.6f", selectedLongitude),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1B2633)
                                    )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Tap pada peta atau drag pin untuk memilih lokasi",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF9CA3AF),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Configure osmdroid with performance optimizations
 */
private fun configureOsmdroid(context: Context) {
    val config = Configuration.getInstance()
    
    // Set user agent (required)
    config.userAgentValue = context.packageName
    
    // Enable tile caching for faster loads
    val cacheDir = File(context.cacheDir, "osmdroid")
    if (!cacheDir.exists()) {
        cacheDir.mkdirs()
    }
    config.osmdroidTileCache = cacheDir
    
    // Increase cache size for better performance (50MB)
    config.tileFileSystemCacheMaxBytes = 50L * 1024 * 1024
    config.tileFileSystemCacheTrimBytes = 40L * 1024 * 1024
    
    // Enable faster tile loading
    config.tileDownloadThreads = 4
    config.tileFileSystemThreads = 4
    
    // Load preferences
    config.load(context, context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))
}

/**
 * Create an optimized MapView with hardware acceleration and proper settings
 */
private fun createOptimizedMapView(context: Context): MapView {
    return MapView(context).apply {
        // Use standard OSM tile source
        setTileSource(TileSourceFactory.MAPNIK)
        
        // Enable multi-touch
        setMultiTouchControls(true)
        
        // Disable built-in zoom controls (we have custom ones)
        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        
        // Performance optimizations
        isTilesScaledToDpi = true
        setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
        
        // Smooth scrolling
        isFlingEnabled = true
        
        // Set minimum and maximum zoom levels
        minZoomLevel = 4.0
        maxZoomLevel = 19.0
        
        // Enable map rotation (optional)
        isHorizontalMapRepetitionEnabled = true
        isVerticalMapRepetitionEnabled = false
    }
}

// Alias for backward compatibility with existing code
@Composable
fun MapLocationPickerDialog(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit,
    onRequestCurrentLocation: (() -> Unit)? = null
) {
    OsmMapPickerDialog(
        initialLatitude = initialLatitude,
        initialLongitude = initialLongitude,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        onRequestCurrentLocation = onRequestCurrentLocation
    )
}
