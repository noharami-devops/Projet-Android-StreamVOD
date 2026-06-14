package com.groupe9.streamvod.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe9.streamvod.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Charger films populaires
            val popularResult = videoRepository.getPopularMovies()
            popularResult.fold(
                onSuccess = { movies ->
                    _uiState.value = _uiState.value.copy(
                        popularMovies = movies,
                        isLoading = false
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message,
                        isLoading = false
                    )
                }
            )

            // Charger films top rated
            val topRatedResult = videoRepository.getTopRatedMovies()
            topRatedResult.fold(
                onSuccess = { movies ->
                    _uiState.value = _uiState.value.copy(
                        topRatedMovies = movies
                    )
                },
                onFailure = {}
            )
        }
    }

    fun retry() {
        loadMovies()
    }
}