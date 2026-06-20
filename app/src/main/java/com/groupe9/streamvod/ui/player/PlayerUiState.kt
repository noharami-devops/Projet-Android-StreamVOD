package com.groupe9.streamvod.ui.player

data class PlayerUiState(
    val isLoading: Boolean = false,
    val videoKey: String? = null,
    val title: String = "",
    val hasError: Boolean = false
)