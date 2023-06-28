package com.example.chernykhhomework.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.R
import com.example.chernykhhomework.domain.entity.LoanRequest
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.entity.ErrorWrapper
import com.example.chernykhhomework.presentation.uistate.NewLoanUIState
import com.example.chernykhhomework.presentation.uistate.RegisterUIState
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
        val errorCode = when (exception) {
            is NoSuchElementException -> R.string.authorization_error

            is SocketTimeoutException -> R.string.connection_time_expired

            is UnknownHostException -> R.string.no_internet_connection

            is HttpException -> R.string.not_suitable_loan

            else -> R.string.unknown_error
        }

        _uiState.value = NewLoanUIState.Error(
            ErrorWrapper(
                errorCode,
                exception::class.java,
                exception.message.toString()
            )
        )
    }
}