package com.groupe9.streamvod.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe9.streamvod.data.repository.FavoriteRepository
import com.groupe9.streamvod.data.repository.VideoRepository
import com.groupe9.streamvod.domain.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Charger le film
            val result = videoRepository.getMovieDetail(movieId)
            result.fold(
                onSuccess = { video ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        video = video
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            )

            // Vérifier si favori
            favoriteRepository.isFavorite(movieId).collect { isFav ->
                _uiState.value = _uiState.value.copy(isFavorite = isFav)
            }
        }
    }

    fun toggleFavorite(video: Video) {
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                favoriteRepository.removeFavorite(video)
            } else {
                favoriteRepository.addFavorite(video)
            }
        }
    }
    fun openTrailer(context: android.content.Context) {
        viewModelScope.launch {
            val result = videoRepository.getMovieTrailer(_uiState.value.video?.id ?: return@launch)
            result.getOrNull()?.let { key ->
                val intent = android.content.Intent(
                    android.content.Intent.ACTION_VIEW,
                    android.net.Uri.parse("https://www.youtube.com/watch?v=$key")
                )
                context.startActivity(intent)
            }
        }
    }
}