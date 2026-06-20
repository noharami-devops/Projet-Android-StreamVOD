package com.groupe9.streamvod.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe9.streamvod.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState

    fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Charger le titre
            val detailResult = videoRepository.getMovieDetail(movieId)
            detailResult.fold(
                onSuccess = { video ->
                    _uiState.value = _uiState.value.copy(title = video.title)
                },
                onFailure = {}
            )

            // Charger la bande-annonce
            val trailerResult = videoRepository.getMovieTrailer(movieId)
            trailerResult.fold(
                onSuccess = { key ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        videoKey = key
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasError = true
                    )
                }
            )
        }
    }

    fun onPlayerError() {
        _uiState.value = _uiState.value.copy(hasError = true)
    }
}