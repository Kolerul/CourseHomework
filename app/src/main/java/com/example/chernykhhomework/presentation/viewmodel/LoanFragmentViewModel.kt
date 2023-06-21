package com.example.chernykhhomework.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.uistate.LoanUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.lang.Exception
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
            } catch (e: NoSuchElementException) {
                _uiState.postValue(
                    LoanUIState.Error("Authorization error, please re-login to your account")
                )
            } catch (e: UnknownHostException) {
                _uiState.postValue(LoanUIState.Error("No internet connection"))
            } catch (e: Exception) {
                _uiState.postValue(LoanUIState.Error("Unknown error ${e::class}: ${e.message}"))
                Log.d("NewLoanViewModel", "${e::class} ${e.message.toString()}")
            }
        }
    }
}