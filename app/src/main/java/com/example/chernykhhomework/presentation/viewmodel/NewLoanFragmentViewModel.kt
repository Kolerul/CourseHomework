package com.example.chernykhhomework.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.data.network.entity.LoanRequest
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.uistate.NewLoanUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class NewLoanFragmentViewModel @Inject constructor(
    private val repository: LoanRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableLiveData<NewLoanUIState>(NewLoanUIState.Initializing)
    val uiState: LiveData<NewLoanUIState>
        get() = _uiState


    fun conditionsRequest() {
        _uiState.value = NewLoanUIState.Loading
        viewModelScope.launch(dispatcher) {
            try {
                val conditions = repository.getLoanConditions()
                _uiState.postValue(NewLoanUIState.Success(conditions))
            } catch (e: Exception) {
                _uiState.postValue(NewLoanUIState.Error(e.message.toString()))
            }

        }
    }

    fun newLoanRequest(loan: LoanRequest) {
        _uiState.value = NewLoanUIState.Loading
        viewModelScope.launch(dispatcher) {
            try {
                repository.requestLoan(loan)
                _uiState.postValue(NewLoanUIState.Success(null))
            } catch (e: Exception) {
                _uiState.postValue(NewLoanUIState.Error(e.message.toString()))
                Log.d("NewLoanViewModel", "${e::class} ${e.message.toString()}")
            }
        }
    }
}