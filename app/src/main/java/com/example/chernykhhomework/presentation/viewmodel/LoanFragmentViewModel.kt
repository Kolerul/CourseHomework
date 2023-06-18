package com.example.chernykhhomework.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.uistate.LoanUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
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
                _uiState.postValue(LoanUIState.Error(e.message.toString()))
            }
        }
    }
}