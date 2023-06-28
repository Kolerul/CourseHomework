package com.example.chernykhhomework.presentation.viewmodel


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.R
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.entity.ErrorWrapper
import com.example.chernykhhomework.presentation.uistate.LoanUIState
import com.example.chernykhhomework.presentation.uistate.LoansListUIState
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class LoanFragmentViewModel @Inject constructor(
    private val repository: LoanRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<LoanUIState>(LoanUIState.Initializing)
    val uiState: LiveData<LoanUIState>
        get() = _uiState

    fun getLoanById(id: Int) {
        _uiState.value = LoanUIState.Loading
        viewModelScope.launch {
            try {
                val loan = repository.getLoanById(id)
                _uiState.value = LoanUIState.Success(loan)
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

            else -> R.string.unknown_error
        }

        _uiState.value = LoanUIState.Error(
            ErrorWrapper(
                errorCode,
                exception::class.java,
                exception.message.toString()
            )
        )
    }
}