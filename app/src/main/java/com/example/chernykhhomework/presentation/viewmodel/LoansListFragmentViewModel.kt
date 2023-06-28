package com.example.chernykhhomework.presentation.viewmodel


import android.app.Application
import android.util.NoSuchPropertyException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.R
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.entity.ErrorWrapper
import com.example.chernykhhomework.presentation.uistate.LoansListUIState
import com.example.chernykhhomework.presentation.uistate.RegisterUIState
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.SocketTimeoutException
import javax.inject.Inject

class LoansListFragmentViewModel @Inject constructor(
    private val repository: LoanRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<LoansListUIState>(LoansListUIState.Initializing)
    val uiState: LiveData<LoansListUIState>
        get() = _uiState

    fun getLoansList() {
        _uiState.value = LoansListUIState.Loading
        viewModelScope.launch {
            try {
                val list = repository.getAllLoans()
                _uiState.value = LoansListUIState.Success(list)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun handleException(exception: Exception) {
        val errorCode = when (exception) {
            is NoSuchElementException -> R.string.authorization_error

            is NoSuchPropertyException -> R.string.no_data

            is SocketTimeoutException -> R.string.connection_time_expired

            else -> R.string.unknown_error
        }

        _uiState.value = LoansListUIState.Error(
            ErrorWrapper(
                errorCode,
                exception::class.java,
                exception.message.toString()
            )
        )
    }
}