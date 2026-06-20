package com.groupe9.streamvod.ui.community

import com.groupe9.streamvod.domain.model.UserVideo

data class CommunityUiState(
    val videos: List<UserVideo> = emptyList(),
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val uploadSuccess: Boolean = false,
    val errorMessage: String? = null
)