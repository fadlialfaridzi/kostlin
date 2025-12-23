package com.example.kostlin.ui.screen.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.core.network.NetworkClient
import com.example.kostlin.core.network.executeRequest
import com.example.kostlin.data.mapper.toDomain
import com.example.kostlin.data.remote.service.ApiService
import com.example.kostlin.domain.model.Kos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// ViewModel
data class SearchUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedType: String? = null,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val results: List<Kos> = emptyList(),
    val error: String? = null
)

class SearchViewModel(
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
    
    fun updateFilters(type: String?, minPrice: Int?, maxPrice: Int?) {
        _uiState.update { 
            it.copy(
                selectedType = type,
                minPrice = minPrice,
                maxPrice = maxPrice
            )
        }
    }
    
    fun search() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val state = _uiState.value
            when (val result = executeRequest(defaultValue = emptyList()) {
                apiService.getKosList(
                    type = state.selectedType,
                    minPrice = state.minPrice,
                    maxPrice = state.maxPrice,
                    search = state.searchQuery.ifBlank { null }
                )
            }) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            results = result.data.map { dto -> dto.toDomain() }
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
    
    fun clearFilters() {
        _uiState.update { 
            it.copy(
                selectedType = null,
                minPrice = null,
                maxPrice = null
            )
        }
    }
}

class SearchViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// Main Screen
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onKosClick: (Kos) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(NetworkClient.apiService)
    )
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    
    // Initial search on load
    LaunchedEffect(Unit) {
        viewModel.search()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cari Kos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FF))
                .padding(padding)
        ) {
            // Search Bar with Filter
            SearchBarWithFilter(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onSearch = { 
                    focusManager.clearFocus()
                    viewModel.search() 
                },
                onFilterClick = { showFilterSheet = true },
                hasActiveFilters = uiState.selectedType != null || uiState.minPrice != null || uiState.maxPrice != null
            )
            
            // Active Filters Chips
            if (uiState.selectedType != null || uiState.minPrice != null || uiState.maxPrice != null) {
                ActiveFiltersRow(
                    selectedType = uiState.selectedType,
                    minPrice = uiState.minPrice,
                    maxPrice = uiState.maxPrice,
                    onClearFilters = {
                        viewModel.clearFilters()
                        viewModel.search()
                    }
                )
            }
            
            // Results count
            Text(
                text = "${uiState.results.size} kos ditemukan",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Content
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF5876FF))
                    }
                }
                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.error!!, color = Color.Red)
                    }
                }
                uiState.results.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", style = MaterialTheme.typography.displayLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tidak ada kos ditemukan", color = Color.Gray)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.results) { kos ->
                            KosSearchResultCard(kos = kos, onClick = { onKosClick(kos) })
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
    
    // Filter Bottom Sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            sheetState = sheetState,
            selectedType = uiState.selectedType,
            minPrice = uiState.minPrice,
            maxPrice = uiState.maxPrice,
            onDismiss = { showFilterSheet = false },
            onApply = { type, minP, maxP ->
                viewModel.updateFilters(type, minP, maxP)
                viewModel.search()
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showFilterSheet = false
                }
            }
        )
    }
}

@Composable
private fun SearchBarWithFilter(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onFilterClick: () -> Unit,
    hasActiveFilters: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Cari nama, alamat, kota...") },
            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = Color.Gray) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, "Clear", tint = Color.Gray)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5876FF),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() })
        )
        
        Surface(
            modifier = Modifier.size(56.dp).clickable { onFilterClick() },
            shape = RoundedCornerShape(12.dp),
            color = if (hasActiveFilters) Color(0xFF5876FF) else Color.White,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.FilterList,
                    "Filter",
                    tint = if (hasActiveFilters) Color.White else Color(0xFF5876FF)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveFiltersRow(
    selectedType: String?,
    minPrice: Int?,
    maxPrice: Int?,
    onClearFilters: () -> Unit
) {
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Filter:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            selectedType?.let {
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text("Kos ${it.replaceFirstChar { c -> c.uppercase() }}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF5876FF).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFF5876FF)
                    )
                )
            }
            
            if (minPrice != null || maxPrice != null) {
                val priceText = when {
                    minPrice != null && maxPrice != null -> "${priceFormatter.format(minPrice)} - ${priceFormatter.format(maxPrice)}"
                    minPrice != null -> "Min ${priceFormatter.format(minPrice)}"
                    else -> "Max ${priceFormatter.format(maxPrice)}"
                }
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text(priceText, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF5876FF).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFF5876FF)
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "Hapus",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFFF44336),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.clickable { onClearFilters() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterBottomSheet(
    sheetState: androidx.compose.material3.SheetState,
    selectedType: String?,
    minPrice: Int?,
    maxPrice: Int?,
    onDismiss: () -> Unit,
    onApply: (type: String?, minPrice: Int?, maxPrice: Int?) -> Unit
) {
    var tempType by remember { mutableStateOf(selectedType) }
    var priceRange by remember { 
        mutableStateOf(
            (minPrice?.toFloat() ?: 0f)..(maxPrice?.toFloat() ?: 5000000f)
        )
    }
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Filter Pencarian", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            
            // Type Filter
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Tipe Kos", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("putra" to "Kos Putra", "putri" to "Kos Putri", "campur" to "Kos Campur").forEach { (value, label) ->
                        FilterChip(
                            selected = tempType == value,
                            onClick = { tempType = if (tempType == value) null else value },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF5876FF),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
            
            // Price Range Filter
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Rentang Harga", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                
                RangeSlider(
                    value = priceRange,
                    onValueChange = { priceRange = it },
                    valueRange = 0f..10000000f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF5876FF),
                        activeTrackColor = Color(0xFF5876FF),
                        inactiveTrackColor = Color(0xFFE0E0E0)
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        priceFormatter.format(priceRange.start.toInt()),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        priceFormatter.format(priceRange.endInclusive.toInt()),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            // Apply Button
            Button(
                onClick = {
                    val minP = if (priceRange.start > 0f) priceRange.start.toInt() else null
                    val maxP = if (priceRange.endInclusive < 10000000f) priceRange.endInclusive.toInt() else null
                    onApply(tempType, minP, maxP)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5876FF))
            ) {
                Text("Terapkan Filter", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun KosSearchResultCard(
    kos: Kos,
    onClick: () -> Unit
) {
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image
            Box(
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp))
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
                        modifier = Modifier.fillMaxSize().background(
                            Brush.linearGradient(listOf(Color(0xFF5876FF), Color(0xFF8B9DFF)))
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏠", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
            
            // Details
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = kos.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, "Location", Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${kos.city} • ${kos.type.name}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
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
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", kos.rating),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
