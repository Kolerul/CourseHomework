package com.example.chernykhhomework.presentation.uistate

import com.example.chernykhhomework.domain.entity.Loan

sealed class LoanUIState {
    object Initializing : LoanUIState()
    object Loading : LoanUIState()
    data class Success(val loan: Loan) : LoanUIState()
    data class Error(val message: String) : LoanUIState()
}