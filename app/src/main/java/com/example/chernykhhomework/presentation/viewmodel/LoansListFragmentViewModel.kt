package com.example.chernykhhomework.presentation.viewmodel

import android.util.Log
import android.util.NoSuchPropertyException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.uistate.LoansListUIState
import com.example.chernykhhomework.presentation.uistate.NewLoanUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class LoansListFragmentViewModel @Inject constructor(
    private val repository: LoanRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableLiveData<LoansListUIState>(LoansListUIState.Initializing)
    val uiState: LiveData<LoansListUIState>
        get() = _uiState

    fun getLoansList(useCache: Boolean) {
        _uiState.value = LoansListUIState.Loading
        viewModelScope.launch(dispatcher) {
            try {
                val list = repository.getAllLoans(useCache)
                _uiState.postValue(LoansListUIState.Success(list))
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun handleException(exception: Exception) {
        when (exception) {
            is NoSuchElementException -> _uiState.postValue(
                LoansListUIState.Error("Authorization error, please re-login to your account")
            )

            is NoSuchPropertyException -> _uiState.postValue(LoansListUIState.Error("No cached data"))
            is SocketTimeoutException -> _uiState.postValue(LoansListUIState.Error("Connection time expired"))
            is UnknownHostException -> _uiState.postValue(LoansListUIState.Error("No internet connection"))
            else -> _uiState.postValue(
                LoansListUIState.Error("Unknown error ${exception::class}: ${exception.message}")
            )
        }
    }
}