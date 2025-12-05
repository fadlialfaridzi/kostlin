package com.example.kostlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kostlin.data.api.model.KosPropertyDto
import com.example.kostlin.data.model.KosProperty
import com.example.kostlin.data.model.KosType
import com.example.kostlin.data.repository.KosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class KosUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val allKos: List<KosProperty> = emptyList(),
    val popularKos: List<KosProperty> = emptyList(),
    val recommendedKos: List<KosProperty> = emptyList(),
    val searchResults: List<KosProperty> = emptyList(),
    val selectedKos: KosProperty? = null
)

class KosViewModel(
    private val kosRepository: KosRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(KosUiState())
    val uiState: StateFlow<KosUiState> = _uiState
    
    init {
        loadPopularKos()
        loadRecommendedKos()
        loadAllKos()
    }
    
    fun loadAllKos(type: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            kosRepository.getAllKos(type = type)
                .onSuccess { kosList ->
                    val convertedKos = kosList.map { it.toKosProperty() }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        allKos = convertedKos
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load kos"
                    )
                }
        }
    }
    
    fun loadPopularKos() {
        viewModelScope.launch {
            kosRepository.getPopularKos()
                .onSuccess { kosList ->
                    val convertedKos = kosList.map { it.toKosProperty() }
                    _uiState.value = _uiState.value.copy(popularKos = convertedKos)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = exception.message ?: "Failed to load popular kos"
                    )
                }
        }
    }
    
    fun loadRecommendedKos() {
        viewModelScope.launch {
            kosRepository.getRecommendedKos()
                .onSuccess { kosList ->
                    val convertedKos = kosList.map { it.toKosProperty() }
                    _uiState.value = _uiState.value.copy(recommendedKos = convertedKos)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = exception.message ?: "Failed to load recommended kos"
                    )
                }
        }
    }
    
    fun searchKos(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            kosRepository.searchKos(query)
                .onSuccess { kosList ->
                    val convertedKos = kosList.map { it.toKosProperty() }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        searchResults = convertedKos
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Search failed"
                    )
                }
        }
    }
    
    fun getKosById(id: Int, onSuccess: (KosProperty) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            kosRepository.getKosById(id)
                .onSuccess { kosDto ->
                    val kosProperty = kosDto.toKosProperty()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedKos = kosProperty
                    )
                    onSuccess(kosProperty)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load kos details"
                    )
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    // Extension function to convert DTO to domain model
    private fun KosPropertyDto.toKosProperty(): KosProperty {
        val safePrice = price ?: "Rp 0/Bulan"
        val safePricePerMonth = pricePerMonth ?: 0

        // Derive rating string/value safely even if backend returns null/empty
        val safeRatingValue = ratingValue
            ?: rating?.toFloatOrNull()
            ?: 0f
        val safeRating = rating ?: safeRatingValue.toString()

        val kosType = when (type?.uppercase()) {
            "PUTRA" -> KosType.PUTRA
            "PUTRI" -> KosType.PUTRI
            "CAMPUR" -> KosType.CAMPUR
            else -> KosType.CAMPUR
        }

        return KosProperty(
            id = id,
            name = name,
            location = location,
            price = safePrice,
            pricePerMonth = safePricePerMonth,
            rating = safeRating,
            ratingValue = safeRatingValue,
            type = kosType,
            facilities = facilities ?: emptyList(),
            description = description ?: "",
            imageUrl = imageUrl,
            isPopular = isPopular,
            isRecommended = isRecommended
        )
    }
}

