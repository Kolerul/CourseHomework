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
import retrofit2.HttpException
import java.lang.Exception
import java.net.UnknownHostException
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
            } catch (e: NoSuchElementException) {
                _uiState.postValue(
                    NewLoanUIState.Error("Authorization error, please re-login to your account")
                )
            } catch (e: UnknownHostException) {
                _uiState.postValue(NewLoanUIState.Error("No internet connection"))
            } catch (e: Exception) {
                _uiState.postValue(NewLoanUIState.Error("Unknown error ${e::class}: ${e.message}"))
            }

        }
    }

    fun newLoanRequest(loan: LoanRequest) {
        _uiState.value = NewLoanUIState.Loading
        viewModelScope.launch(dispatcher) {
            try {
                repository.requestLoan(loan)
                _uiState.postValue(NewLoanUIState.Success(null))
            } catch (e: HttpException) {
                _uiState.postValue(
                    NewLoanUIState.Error("The loan amount does not correspond to the maximum possible")
                )
            } catch (e: NoSuchElementException) {
                _uiState.postValue(
                    NewLoanUIState.Error("Authorization error, please re-login to your account")
                )
            } catch (e: UnknownHostException) {
                _uiState.postValue(NewLoanUIState.Error("No internet connection"))
            } catch (e: Exception) {
                _uiState.postValue(NewLoanUIState.Error("Unknown error ${e::class}: ${e.message}"))
                Log.d("NewLoanViewModel", "${e::class} ${e.message.toString()}")
            }
        }
    }
}