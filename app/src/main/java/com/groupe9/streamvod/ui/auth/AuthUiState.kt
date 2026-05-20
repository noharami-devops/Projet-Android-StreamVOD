package com.groupe9.streamvod.ui.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val name: String = ""
)