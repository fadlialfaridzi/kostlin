package com.example.kostlin.ui.screen.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.core.network.executeRequest
import com.example.kostlin.data.remote.dto.kos.CreateKosRequestDto
import com.example.kostlin.data.remote.dto.kos.FacilityDto
import com.example.kostlin.data.remote.service.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditKosUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class EditKosViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditKosUiState())
    val uiState: StateFlow<EditKosUiState> = _uiState.asStateFlow()

    fun updateKos(
        kosId: Int,
        name: String,
        description: String,
        address: String,
        city: String,
        pricePerMonth: Int,
        type: String,
        latitude: Double?,
        longitude: Double?,
        facilities: List<String>
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val request = CreateKosRequestDto(
                name = name,
                description = description,
                address = address,
                city = city,
                pricePerMonth = pricePerMonth,
                type = type,
                latitude = latitude,
                longitude = longitude,
                facilities = facilities.map { FacilityDto(name = it) }
            )
            
            when (val result = executeRequest { apiService.updateKos(kosId, request) }) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { EditKosUiState() }
    }
}

class EditKosViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditKosViewModel::class.java)) {
            return EditKosViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
