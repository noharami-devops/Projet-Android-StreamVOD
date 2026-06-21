package com.groupe9.streamvod.ui.community

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe9.streamvod.data.repository.UserVideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val userVideoRepository: UserVideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState

    init {
        loadVideos()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            userVideoRepository.getAllUserVideos().collect { videos ->
                _uiState.value = _uiState.value.copy(
                    videos = videos,
                    isLoading = false
                )
            }
        }
    }

    fun uploadVideo(context: Context, uri: Uri, title: String, description: String) {
        if (title.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Donnez un titre à votre vidéo"
            )
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true, errorMessage = null)
            val result = userVideoRepository.uploadVideo(context, uri, title, description)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isUploading = false, uploadSuccess = true)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isUploading = false, errorMessage = e.message)
                }
            )
        }
    }

    fun toggleLike(videoId: String, currentLikes: Int) {
        viewModelScope.launch {
            userVideoRepository.toggleLike(videoId, currentLikes)
        }
    }

    fun resetUploadState() {
        _uiState.value = _uiState.value.copy(uploadSuccess = false, errorMessage = null)
    }
    val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    fun deleteVideo(videoId: String, uploaderId: String) {
        viewModelScope.launch {
            val result = userVideoRepository.deleteVideo(videoId, uploaderId)
            result.fold(
                onSuccess = {},
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
            )
        }
    }
}