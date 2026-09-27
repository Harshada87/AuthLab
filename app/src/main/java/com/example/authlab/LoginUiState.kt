package com.example.authlab

sealed interface LoginUiState {
    data object Loading: LoginUiState
    data object Idle: LoginUiState
    data class Success(val user: User) : LoginUiState
    data class Failure (val message: String) : LoginUiState
    data class Locked(val preview: String) : LoginUiState
    data class Revealed(val note: String): LoginUiState
}