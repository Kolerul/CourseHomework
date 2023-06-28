package com.example.chernykhhomework.presentation.uistate

import com.example.chernykhhomework.domain.entity.Loan
import com.example.chernykhhomework.presentation.entity.ErrorWrapper

sealed class LoanUIState {
    object Initializing : LoanUIState()
    object Loading : LoanUIState()
    data class Success(val loan: Loan) : LoanUIState()
    data class Error(val error: ErrorWrapper) : LoanUIState()
}