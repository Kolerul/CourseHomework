package com.example.chernykhhomework.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.data.network.entity.LoanRequest
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.uistate.NewLoanUIState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class NewLoanFragmentViewModel @Inject constructor(
    private val repository: LoanRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<NewLoanUIState>(NewLoanUIState.Initializing)
    val uiState: LiveData<NewLoanUIState>
        get() = _uiState


    fun conditionsRequest() {
        _uiState.value = NewLoanUIState.Loading
        viewModelScope.launch {
            try {
                val conditions = repository.getLoanConditions()
                _uiState.value = NewLoanUIState.Success(conditions)
            } catch (e: Exception) {
                handleException(e)
            }

        }
    }

    fun newLoanRequest(loan: LoanRequest) {
        _uiState.value = NewLoanUIState.Loading
        viewModelScope.launch {
            try {
                repository.requestLoan(loan)
                _uiState.value = NewLoanUIState.Success(null)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun handleException(exception: Exception) {
        when (exception) {
            is NoSuchElementException -> _uiState.value =
                NewLoanUIState.Error("Authorization error, please re-login to your account")

            is SocketTimeoutException -> _uiState.value =
                NewLoanUIState.Error("Connection time expired")

            is UnknownHostException -> _uiState.value =
                NewLoanUIState.Error("No internet connection")

            is HttpException -> _uiState.value =
                NewLoanUIState.Error("The loan does not meet acceptable conditions")

            else -> _uiState.value =
                NewLoanUIState.Error("Unknown error ${exception::class}: ${exception.message}")
        }
    }
}