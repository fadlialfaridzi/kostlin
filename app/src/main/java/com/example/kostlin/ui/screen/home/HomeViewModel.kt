package com.example.kostlin.ui.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kostlin.core.location.LocationHelper
import com.example.kostlin.core.location.UserLocation
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.core.network.map
import com.example.kostlin.data.model.KosProperty
import com.example.kostlin.domain.model.KosCategory
import com.example.kostlin.domain.repository.KosRepository
import com.example.kostlin.ui.model.toKosPropertyList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: KosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private var locationHelper: LocationHelper? = null

    init {
        viewModelScope.launch {
            refreshHome()
        }
    }
    
    fun initLocation(context: Context) {
        if (locationHelper == null) {
            locationHelper = LocationHelper(context)
        }
    }
    
    fun hasLocationPermission(): Boolean {
        return locationHelper?.hasLocationPermission() ?: false
    }
    
    fun fetchUserLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocationLoading = true) }
            val location = locationHelper?.getCurrentLocation()
            _uiState.update { 
                it.copy(
                    userLocation = location,
                    userCity = location?.cityName ?: "Unknown",
                    isLocationLoading = false,
                    locationPermissionGranted = location != null
                )
            }
        }
    }
    
    fun onLocationPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(locationPermissionGranted = granted) }
        if (granted) {
            fetchUserLocation()
        }
    }

    fun refreshHome(forcePopular: Boolean = false) {
        viewModelScope.launch {
            loadHomeData(_uiState.value.selectedCategory, forcePopular)
        }
    }

    fun selectCategory(category: KosCategory?) {
        viewModelScope.launch {
            loadHomeData(category, forcePopular = false)
        }
    }

    private suspend fun loadHomeData(category: KosCategory?, forcePopular: Boolean) {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                selectedCategory = category
            )
        }

        val currentPopular = _uiState.value.popularKos
        val shouldFetchPopular = forcePopular || currentPopular.isEmpty()

        val popularResult = if (shouldFetchPopular) {
            repository.getPopularKos().map { it.toKosPropertyList() }
        } else {
            ApiResult.success(currentPopular)
        }

        val typeQuery = category?.name?.lowercase()
        val listResult = repository.getKosList(type = typeQuery).map { it.toKosPropertyList() }

        val errorMessage = when {
            popularResult is ApiResult.Error -> popularResult.message
            listResult is ApiResult.Error -> listResult.message
            else -> null
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = errorMessage,
                popularKos = if (popularResult is ApiResult.Success) popularResult.data else it.popularKos,
                recommendations = if (listResult is ApiResult.Success) listResult.data else it.recommendations
            )
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val popularKos: List<KosProperty> = emptyList(),
    val recommendations: List<KosProperty> = emptyList(),
    val selectedCategory: KosCategory? = null,
    // Location state
    val userLocation: UserLocation? = null,
    val userCity: String = "Padang",
    val isLocationLoading: Boolean = false,
    val locationPermissionGranted: Boolean = false
)
