package com.example.chernykhhomework.presentation.uistate

import com.example.chernykhhomework.data.network.entity.Auth

sealed class RegisterUIState {
    object Initializing : RegisterUIState()
    object Loading : RegisterUIState()
    data class Success(val user: Auth?, val firstEntry: Boolean) : RegisterUIState()
    data class Error(val message: String) : RegisterUIState()
}