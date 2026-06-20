package com.groupe9.streamvod.ui.search

import com.groupe9.streamvod.domain.model.Video

data class SearchUiState(
    val query: String = "",
    val results: List<Video> = emptyList(),
    val isLoading: Boolean = false
)