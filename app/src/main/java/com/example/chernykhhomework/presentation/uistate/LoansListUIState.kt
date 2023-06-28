package com.example.chernykhhomework.presentation.uistate

import com.example.chernykhhomework.domain.entity.Loan
import com.example.chernykhhomework.presentation.entity.ErrorWrapper

sealed class LoansListUIState {
    object Initializing : LoansListUIState()
    object Loading : LoansListUIState()
    data class Success(val loansList: List<Loan>) : LoansListUIState()
    data class Error(val error: ErrorWrapper) : LoansListUIState()
}