package com.example.kostlin.ui.screen.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.domain.model.Booking
import com.example.kostlin.domain.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookingViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    fun fetchBookings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = repository.getBookings()) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            bookings = result.data,
                            errorMessage = null
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun createBooking(
        kosId: Int,
        bookingType: BookingType,
        roomQuantity: Int,
        totalPrice: Int,
        note: String? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            
            val typeString = when (bookingType) {
                BookingType.MONTHLY -> "monthly"
                BookingType.YEARLY -> "yearly"
            }
            
            when (val result = repository.createBooking(
                kosId = kosId,
                bookingType = typeString,
                roomQuantity = roomQuantity,
                totalPrice = totalPrice,
                note = note
            )) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isSubmitting = false,
                            lastCreatedBooking = result.data,
                            errorMessage = null
                        )
                    }
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class BookingUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val lastCreatedBooking: Booking? = null,
    val errorMessage: String? = null
)

class BookingViewModelFactory(
    private val repository: BookingRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            return BookingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
