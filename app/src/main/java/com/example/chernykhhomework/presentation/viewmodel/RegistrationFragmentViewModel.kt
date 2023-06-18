package com.example.chernykhhomework.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.data.network.entity.Auth
import com.example.chernykhhomework.domain.repository.AuthRepository
import com.example.chernykhhomework.presentation.uistate.RegisterUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationFragmentViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableLiveData<RegisterUIState>(RegisterUIState.Initializing)
    val uiState: LiveData<RegisterUIState>
        get() = _uiState

    fun register(name: String, password: String) {
        _uiState.value = RegisterUIState.Loading
        val auth = Auth(name, password)
        viewModelScope.launch(dispatcher) {
            try {
                repository.registration(auth)
                _uiState.postValue(RegisterUIState.Success)
            } catch (e: Exception) {
                _uiState.postValue(RegisterUIState.Error(e.message.toString()))
            }

        }
    }

    fun logIn(name: String, password: String) {
        _uiState.value = RegisterUIState.Loading
        val auth = Auth(name, password)
        viewModelScope.launch(dispatcher) {
            try {
                repository.login(auth)
                _uiState.postValue(RegisterUIState.Success)
            } catch (e: Exception) {
                _uiState.postValue(RegisterUIState.Error(e.message.toString()))
            }

        }
    }

}