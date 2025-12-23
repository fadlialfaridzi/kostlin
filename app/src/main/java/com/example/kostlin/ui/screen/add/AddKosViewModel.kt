package com.example.kostlin.ui.screen.add

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.domain.model.Kos
import com.example.kostlin.domain.repository.KosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddKosUiState(
    val isLoading: Boolean = false,
    val isUploadingImages: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val warningMessage: String? = null, // For partial success (kos created but images failed)
    val createdKos: Kos? = null,
    val uploadedImageUrls: List<String> = emptyList()
)

class AddKosViewModel(
    private val repository: KosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddKosUiState())
    val uiState: StateFlow<AddKosUiState> = _uiState.asStateFlow()

    fun createKosWithImages(
        name: String,
        description: String?,
        address: String,
        city: String,
        latitude: Double?,
        longitude: Double?,
        pricePerMonth: Int,
        type: String,
        facilities: List<Pair<String, String?>>,
        images: List<Bitmap>
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, warningMessage = null, isSuccess = false) }
            
            val result = repository.createKos(
                name = name,
                description = description,
                address = address,
                city = city,
                latitude = latitude,
                longitude = longitude,
                pricePerMonth = pricePerMonth,
                type = type,
                facilities = facilities
            )
            
            when (result) {
                is ApiResult.Success -> {
                    val kosId = result.data.id.toIntOrNull() ?: 0
                    
                    // Upload images if available
                    if (images.isNotEmpty() && kosId > 0) {
                        _uiState.update { it.copy(isLoading = false, isUploadingImages = true) }
                        
                        val uploadResult = repository.uploadKosImages(kosId, images)
                        when (uploadResult) {
                            is ApiResult.Success -> {
                                _uiState.update {
                                    it.copy(
                                        isUploadingImages = false,
                                        isSuccess = true,
                                        createdKos = result.data,
                                        uploadedImageUrls = uploadResult.data,
                                        errorMessage = null,
                                        warningMessage = null
                                    )
                                }
                            }
                            is ApiResult.Error -> {
                                // Kos was created but images failed - show success with warning
                                _uiState.update {
                                    it.copy(
                                        isUploadingImages = false,
                                        isSuccess = true,
                                        createdKos = result.data,
                                        errorMessage = null,
                                        warningMessage = "Kos berhasil dibuat, tapi gagal upload gambar"
                                    )
                                }
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                createdKos = result.data,
                                errorMessage = null,
                                warningMessage = null
                            )
                        }
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = result.message ?: "Gagal mendaftarkan kos"
                        )
                    }
                }
            }
        }
    }
    
    fun resetState() {
        _uiState.update { AddKosUiState() }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, warningMessage = null) }
    }
}
