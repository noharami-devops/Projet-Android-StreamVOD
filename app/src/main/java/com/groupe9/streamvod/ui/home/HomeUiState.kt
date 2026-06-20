package com.groupe9.streamvod.ui.home

import com.groupe9.streamvod.domain.model.Video

data class HomeUiState(
    val isLoading: Boolean = false,
    val popularMovies: List<Video> = emptyList(),
    val topRatedMovies: List<Video> = emptyList(),
    val errorMessage: String? = null
)