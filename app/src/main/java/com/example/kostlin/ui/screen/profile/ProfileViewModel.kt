package com.example.kostlin.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.core.network.executeRequest
import com.example.kostlin.data.mapper.toDomain
import com.example.kostlin.data.remote.service.ApiService
import com.example.kostlin.domain.model.Booking
import com.example.kostlin.domain.model.Kos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun fetchMyKos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingKos = true) }
            when (val result = executeRequest(defaultValue = emptyList()) { apiService.getMyKos() }) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoadingKos = false,
                            ownedKos = result.data.map { dto -> dto.toDomain() }
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoadingKos = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun fetchBookingHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true) }
            when (val result = executeRequest(defaultValue = emptyList()) { apiService.getBookings() }) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoadingHistory = false,
                            bookingHistory = result.data.map { dto -> dto.toDomain() }
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoadingHistory = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun fetchBookingRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRequests = true) }
            when (val result = executeRequest(defaultValue = emptyList()) { apiService.getBookingRequests() }) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoadingRequests = false,
                            bookingRequests = result.data.map { dto -> dto.toDomain() }
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoadingRequests = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun confirmBooking(bookingId: Int) {
        updateBookingStatus(bookingId, "confirmed")
    }

    fun cancelBooking(bookingId: Int) {
        updateBookingStatus(bookingId, "cancelled")
    }

    private fun updateBookingStatus(bookingId: Int, status: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            when (val result = executeRequest { 
                apiService.updateBookingStatus(bookingId, mapOf("status" to status)) 
            }) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isUpdating = false,
                            successMessage = "Status booking berhasil diubah"
                        )
                    }
                    // Refresh the requests list
                    fetchBookingRequests()
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isUpdating = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true) }
            when (val result = executeRequest { apiService.getProfile() }) {
                is ApiResult.Success -> {
                    val user = result.data
                    _uiState.update { 
                        it.copy(
                            isLoadingUser = false,
                            userName = user.fullName,
                            userEmail = user.email,
                            userPhone = user.phoneNumber ?: ""
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoadingUser = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun toggleKosActive(kosId: Int, isCurrentlyActive: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            when (val result = executeRequest { 
                apiService.toggleKosActive(kosId, mapOf("isActive" to !isCurrentlyActive)) 
            }) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isUpdating = false,
                            successMessage = if (!isCurrentlyActive) "Kos diaktifkan" else "Kos dinonaktifkan"
                        )
                    }
                    fetchMyKos()
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isUpdating = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun deleteKos(kosId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            when (val result = executeRequest { apiService.deleteKos(kosId) }) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isUpdating = false,
                            successMessage = "Kos berhasil dihapus"
                        )
                    }
                    fetchMyKos()
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isUpdating = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun createReview(kosId: Int, rating: Double, comment: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            val request = com.example.kostlin.data.remote.dto.review.ReviewRequestDto(
                rating = rating,
                comment = comment
            )
            when (val result = executeRequest { apiService.createReview(kosId.toString(), request) }) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isUpdating = false,
                            successMessage = "Rating berhasil dikirim"
                        )
                    }
                    fetchBookingHistory()
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isUpdating = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            val body = mapOf(
                "currentPassword" to currentPassword,
                "newPassword" to newPassword
            )
            when (val result = executeRequest { apiService.changePassword(body) }) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isUpdating = false,
                            successMessage = "Password berhasil diubah"
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isUpdating = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}

data class ProfileUiState(
    val isLoadingKos: Boolean = false,
    val isLoadingHistory: Boolean = false,
    val isLoadingRequests: Boolean = false,
    val isLoadingUser: Boolean = false,
    val isUpdating: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val ownedKos: List<Kos> = emptyList(),
    val bookingHistory: List<Booking> = emptyList(),
    val bookingRequests: List<Booking> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ProfileViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

