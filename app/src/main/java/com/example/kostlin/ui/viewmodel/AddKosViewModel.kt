package com.example.kostlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kostlin.data.repository.KosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AddKosUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AddKosViewModel(
    private val kosRepository: KosRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddKosUiState())
    val uiState: StateFlow<AddKosUiState> = _uiState
    
    fun createKos(
        name: String,
        location: String,
        pricePerMonth: Int,
        type: String,
        description: String? = null,
        ownerEmail: String? = null,
        ownerPhone: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        facilities: List<String> = emptyList(),
        imageUrl: String? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )
            
            kosRepository.createKos(
                name = name,
                location = location,
                pricePerMonth = pricePerMonth,
                type = type,
                description = description,
                ownerEmail = ownerEmail,
                ownerPhone = ownerPhone,
                latitude = latitude,
                longitude = longitude,
                facilities = facilities,
                imageUrl = imageUrl
            )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Kos berhasil didaftarkan!"
                    )
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = exception.message ?: "Gagal mendaftarkan kos"
                    )
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null, isSuccess = false)
    }
}

