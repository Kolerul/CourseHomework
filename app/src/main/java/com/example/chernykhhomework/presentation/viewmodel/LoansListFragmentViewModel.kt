package com.example.chernykhhomework.presentation.viewmodel


import android.app.Application
import android.util.NoSuchPropertyException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.R
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.uistate.LoansListUIState
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.SocketTimeoutException
import javax.inject.Inject

class LoansListFragmentViewModel @Inject constructor(
    private val repository: LoanRepository,
    private val application: Application
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
        when (exception) {
            is NoSuchElementException ->
                _uiState.value =
                    LoansListUIState.Error(application.getString(R.string.authorization_error))

            is NoSuchPropertyException ->
                _uiState.value = LoansListUIState.Error(application.getString(R.string.no_data))

            is SocketTimeoutException ->
                _uiState.value =
                    LoansListUIState.Error(application.getString(R.string.connection_time_expired))

            else -> _uiState.value =
                LoansListUIState.Error(
                    application.getString(
                        R.string.unknown_error,
                        exception::class.toString(),
                        exception.message
                    )
                )
        }
    }
}