package com.example.kostlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kostlin.data.api.model.FavoriteKosDto
import com.example.kostlin.data.model.FavoriteKos
import com.example.kostlin.data.model.FavoriteStatus
import com.example.kostlin.data.model.KosProperty
import com.example.kostlin.data.model.KosType
import com.example.kostlin.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val favorites: List<FavoriteKos> = emptyList(),
    val favoriteStatusMap: Map<Int, Boolean> = emptyMap()
)

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState
    
    init {
        loadFavorites()
    }
    
    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            favoriteRepository.getUserFavorites()
                .onSuccess { favoritesList ->
                    val convertedFavorites = favoritesList.map { it.toFavoriteKos() }
                    val statusMap = convertedFavorites.associate { it.kosProperty.id to true }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        favorites = convertedFavorites,
                        favoriteStatusMap = statusMap
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load favorites"
                    )
                }
        }
    }
    
    fun addToFavorite(kosId: Int) {
        viewModelScope.launch {
            favoriteRepository.addToFavorite(kosId)
                .onSuccess {
                    loadFavorites() // Reload to get updated list
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = exception.message ?: "Failed to add to favorites"
                    )
                }
        }
    }
    
    fun removeFromFavorite(kosId: Int) {
        viewModelScope.launch {
            favoriteRepository.removeFromFavorite(kosId)
                .onSuccess {
                    loadFavorites() // Reload to get updated list
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = exception.message ?: "Failed to remove from favorites"
                    )
                }
        }
    }
    
    fun checkFavoriteStatus(kosId: Int) {
        viewModelScope.launch {
            favoriteRepository.checkFavoriteStatus(kosId)
                .onSuccess { isFavorite ->
                    val currentMap = _uiState.value.favoriteStatusMap.toMutableMap()
                    currentMap[kosId] = isFavorite
                    _uiState.value = _uiState.value.copy(favoriteStatusMap = currentMap)
                }
                .onFailure {
                    // Silently fail for status check
                }
        }
    }
    
    fun isFavorite(kosId: Int): Boolean {
        return _uiState.value.favoriteStatusMap[kosId] ?: false
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    // Extension function to convert DTO to domain model
    private fun FavoriteKosDto.toFavoriteKos(): FavoriteKos {
        val status = when (status.uppercase()) {
            "ACTIVE" -> FavoriteStatus.ACTIVE
            "REMOVED" -> FavoriteStatus.REMOVED
            else -> FavoriteStatus.ACTIVE
        }
        
        val kosType = when (kosProperty.type?.uppercase()) {
            "PUTRA" -> KosType.PUTRA
            "PUTRI" -> KosType.PUTRI
            "CAMPUR" -> KosType.CAMPUR
            else -> KosType.CAMPUR
        }
        
        val kosPropertyDomain = KosProperty(
            id = kosProperty.id,
            name = kosProperty.name,
            location = kosProperty.location,
            price = kosProperty.price ?: "Rp 0/Bulan",
            pricePerMonth = kosProperty.pricePerMonth ?: 0,
            rating = kosProperty.rating ?: (kosProperty.ratingValue ?: 0f).toString(),
            ratingValue = kosProperty.ratingValue ?: 0f,
            type = kosType,
            facilities = kosProperty.facilities ?: emptyList(),
            description = kosProperty.description ?: "",
            imageUrl = kosProperty.imageUrl,
            isPopular = kosProperty.isPopular,
            isRecommended = kosProperty.isRecommended
        )
        
        return FavoriteKos(
            id = id,
            kosProperty = kosPropertyDomain,
            dateAdded = dateAdded,
            status = status
        )
    }
}

