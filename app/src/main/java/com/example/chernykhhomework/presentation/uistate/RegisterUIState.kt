package com.example.chernykhhomework.presentation.uistate

import com.example.chernykhhomework.presentation.entity.ErrorWrapper

sealed class RegisterUIState {
    object Initializing : RegisterUIState()
    object Loading : RegisterUIState()
    data class Success(
        val userName: String?,
        val firstEntry: Boolean
    ) : RegisterUIState()

    data class Error(val error: ErrorWrapper) : RegisterUIState()
}