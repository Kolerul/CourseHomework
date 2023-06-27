package com.example.chernykhhomework.presentation.viewmodel


import android.util.NoSuchPropertyException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.uistate.LoansListUIState
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class LoansListFragmentViewModel @Inject constructor(
    private val repository: LoanRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<LoansListUIState>(LoansListUIState.Initializing)
    val uiState: LiveData<LoansListUIState>
        get() = _uiState

    fun getLoansList(useCache: Boolean) {
        _uiState.value = LoansListUIState.Loading
        viewModelScope.launch {
            try {
                val list = repository.getAllLoans(useCache)
                _uiState.value = LoansListUIState.Success(list)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun handleException(exception: Exception) {
        when (exception) {
            is NoSuchElementException -> _uiState.value =
                LoansListUIState.Error("Authorization error, please re-login to your account")

            is NoSuchPropertyException -> _uiState.value = LoansListUIState.Error("No cached data")
            is SocketTimeoutException -> _uiState.value =
                LoansListUIState.Error("Connection time expired")

            is UnknownHostException -> _uiState.value =
                LoansListUIState.Error("No internet connection")

            else -> _uiState.value =
                LoansListUIState.Error("Unknown error ${exception::class}: ${exception.message}")
        }
    }
}