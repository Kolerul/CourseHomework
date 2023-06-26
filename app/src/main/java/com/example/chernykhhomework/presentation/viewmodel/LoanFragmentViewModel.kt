package com.example.chernykhhomework.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.uistate.LoanUIState
import com.example.chernykhhomework.presentation.uistate.LoansListUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class LoanFragmentViewModel @Inject constructor(
    private val repository: LoanRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableLiveData<LoanUIState>(LoanUIState.Initializing)
    val uiState: LiveData<LoanUIState>
        get() = _uiState

    fun getLoanById(id: Int) {
        _uiState.value = LoanUIState.Loading
        viewModelScope.launch(dispatcher) {
            try {
                val loan = repository.getLoanById(id)
                _uiState.postValue(LoanUIState.Success(loan))
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun handleException(exception: Exception) {
        when (exception) {
            is NoSuchElementException -> _uiState.postValue(
                LoanUIState.Error("Authorization error, please re-login to your account")
            )

            is SocketTimeoutException -> _uiState.postValue(LoanUIState.Error("Connection time expired"))
            is UnknownHostException -> _uiState.postValue(LoanUIState.Error("No internet connection"))
            else -> _uiState.postValue(
                LoanUIState.Error("Unknown error ${exception::class}: ${exception.message}")
            )
        }
    }
}