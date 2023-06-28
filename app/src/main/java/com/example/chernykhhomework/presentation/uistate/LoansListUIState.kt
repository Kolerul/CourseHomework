package com.example.chernykhhomework.presentation.uistate

import com.example.chernykhhomework.domain.entity.Loan

sealed class LoansListUIState {
    object Initializing : LoansListUIState()
    object Loading : LoansListUIState()
    data class Success(val loansList: List<Loan>) : LoansListUIState()
    data class Error(val message: String) : LoansListUIState()
}