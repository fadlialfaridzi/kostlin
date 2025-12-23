package com.example.kostlin.ui.screen.autentikasi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.domain.model.User
import com.example.kostlin.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.login(email, password)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loggedInUser = result.data,
                            errorMessage = null
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun register(fullName: String, email: String, password: String, phoneNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.register(fullName, email, password, phoneNumber)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registrationSuccessName = result.data.fullName,
                            registrationSuccessEmail = result.data.email,
                            errorMessage = null
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun consumeAuthSuccess() {
        _uiState.update { it.copy(loggedInUser = null) }
    }

    fun consumeRegistrationSuccess() {
        _uiState.update {
            it.copy(registrationSuccessEmail = null, registrationSuccessName = null)
        }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInUser: User? = null,
    val registrationSuccessName: String? = null,
    val registrationSuccessEmail: String? = null
)
