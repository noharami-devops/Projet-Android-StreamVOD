package com.groupe9.streamvod.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.groupe9.streamvod.data.repository.UserVideoRepository
import com.groupe9.streamvod.domain.model.UserVideo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyVideosUiState(
    val isLoading: Boolean = false,
    val videos: List<UserVideo> = emptyList()
)

@HiltViewModel
class MyVideosViewModel @Inject constructor(
    private val userVideoRepository: UserVideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyVideosUiState())
    val uiState: StateFlow<MyVideosUiState> = _uiState

    val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadMyVideos()
    }

    private fun loadMyVideos() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            userVideoRepository.getUserVideosByUploader(userId).collect { videos ->
                _uiState.value = _uiState.value.copy(videos = videos, isLoading = false)
            }
        }
    }

    fun deleteVideo(videoId: String, uploaderId: String) {
        viewModelScope.launch {
            userVideoRepository.deleteVideo(videoId, uploaderId)
        }
    }
}