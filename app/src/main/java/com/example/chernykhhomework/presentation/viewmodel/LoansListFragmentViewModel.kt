package com.example.chernykhhomework.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.uistate.LoansListUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.UnknownHostException
import javax.inject.Inject

class LoansListFragmentViewModel @Inject constructor(
    private val repository: LoanRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableLiveData<LoansListUIState>(LoansListUIState.Initializing)
    val uiState: LiveData<LoansListUIState>
        get() = _uiState

    fun getLoansList() {
        _uiState.value = LoansListUIState.Loading
        viewModelScope.launch(dispatcher) {
            try {
                val list = repository.getAllLoans()
                _uiState.postValue(LoansListUIState.Success(list))
            } catch (e: NoSuchElementException) {
                _uiState.postValue(
                    LoansListUIState.Error("Authorization error, please re-login to your account")
                )
            } catch (e: UnknownHostException) {
                _uiState.postValue(LoansListUIState.Error("No internet connection"))
            } catch (e: Exception) {
                _uiState.postValue(LoansListUIState.Error("Unknown error ${e::class}: ${e.message}"))
                Log.d("NewLoanViewModel", "${e::class} ${e.message.toString()}")
            }
        }
    }

}