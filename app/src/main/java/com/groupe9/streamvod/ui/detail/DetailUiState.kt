package com.groupe9.streamvod.ui.detail

import com.groupe9.streamvod.domain.model.Video

data class DetailUiState(
    val isLoading: Boolean = false,
    val video: Video? = null,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)