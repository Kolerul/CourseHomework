package com.example.chernykhhomework.presentation.uistate

sealed class RegisterUIState {
    object Initializing : RegisterUIState()
    object Loading : RegisterUIState()
    object Success : RegisterUIState()
    data class Error(val message: String) : RegisterUIState()
}