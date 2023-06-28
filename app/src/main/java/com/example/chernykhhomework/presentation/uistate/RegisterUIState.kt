package com.example.chernykhhomework.presentation.uistate

sealed class RegisterUIState {
    object Initializing : RegisterUIState()
    object Loading : RegisterUIState()
    data class Success(val userName: String?, val firstEntry: Boolean) : RegisterUIState()
    data class Error(val message: String) : RegisterUIState()
}