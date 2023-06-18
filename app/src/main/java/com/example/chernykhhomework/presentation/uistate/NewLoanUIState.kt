package com.example.chernykhhomework.presentation.uistate

import com.example.chernykhhomework.data.network.entity.LoanConditions

sealed class NewLoanUIState {
    object Initializing : NewLoanUIState()
    object Loading : NewLoanUIState()
    data class Success(val conditions: LoanConditions?) : NewLoanUIState()
    data class Error(val message: String) : NewLoanUIState()
}