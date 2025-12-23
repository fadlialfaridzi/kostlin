package com.example.kostlin.ui.screen.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kostlin.core.network.ApiResult
import com.example.kostlin.domain.model.FavoriteEntry
import com.example.kostlin.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val repository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        fetchFavorites()
    }

    fun fetchFavorites(includeHistory: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.getFavorites(includeHistory)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, favorites = result.data) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun addFavorite(kosId: Int) {
        android.util.Log.d("FavoriteVM", "addFavorite called with kosId: $kosId")
        viewModelScope.launch {
            when (val result = repository.addFavorite(kosId)) {
                is ApiResult.Success -> {
                    android.util.Log.d("FavoriteVM", "addFavorite SUCCESS")
                    fetchFavorites()
                }
                is ApiResult.Error -> {
                    android.util.Log.e("FavoriteVM", "addFavorite ERROR: ${result.message}")
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }

    fun removeFavorite(kosId: Int) {
        android.util.Log.d("FavoriteVM", "removeFavorite called with kosId: $kosId")
        viewModelScope.launch {
            when (val result = repository.removeFavorite(kosId)) {
                is ApiResult.Success -> {
                    android.util.Log.d("FavoriteVM", "removeFavorite SUCCESS")
                    fetchFavorites()
                }
                is ApiResult.Error -> {
                    android.util.Log.e("FavoriteVM", "removeFavorite ERROR: ${result.message}")
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }
}

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val favorites: List<FavoriteEntry> = emptyList(),
    val errorMessage: String? = null
)
