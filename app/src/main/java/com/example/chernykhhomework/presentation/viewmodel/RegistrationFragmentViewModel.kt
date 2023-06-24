package com.example.chernykhhomework.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.data.network.entity.Auth
import com.example.chernykhhomework.domain.repository.AuthRepository
import com.example.chernykhhomework.presentation.uistate.RegisterUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
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
                val user = repository.registration(auth)
                _uiState.postValue(RegisterUIState.Success(user, true))
            } catch (e: SocketTimeoutException) {
                _uiState.postValue(RegisterUIState.Error("Connection time expired"))
            } catch (e: UnknownHostException) {
                _uiState.postValue(RegisterUIState.Error("No internet connection"))
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> {
                        _uiState.postValue(RegisterUIState.Error("This login is already occupied"))
                    }

                    404 -> {
                        _uiState.postValue(RegisterUIState.Error("Wrong login or password"))
                    }
                }
            } catch (e: Exception) {
                Log.d("RegistrationViewModel", "${e::class}")
                _uiState.postValue(RegisterUIState.Error("Unknown error ${e::class}: ${e.message}"))
            }

        }
    }

    fun logIn(name: String, password: String) {
        _uiState.value = RegisterUIState.Loading
        val auth = Auth(name, password)
        viewModelScope.launch(dispatcher) {
            try {
                val user = repository.login(auth)
                _uiState.postValue(RegisterUIState.Success(user, false))
            } catch (e: SocketTimeoutException) {
                _uiState.postValue(RegisterUIState.Error("Connection time expired"))
            } catch (e: UnknownHostException) {
                _uiState.postValue(RegisterUIState.Error("No internet connection"))
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> {
                        _uiState.postValue(RegisterUIState.Error("This login is already occupied"))
                    }

                    404 -> {
                        _uiState.postValue(RegisterUIState.Error("Wrong login or password"))
                    }
                }
            } catch (e: Exception) {
                Log.d("RegistrationViewModel", "${e::class}")
                _uiState.postValue(RegisterUIState.Error("Unknown error ${e::class}: ${e.message}"))
            }

        }
    }

    fun autoLogIn() {
        _uiState.value = RegisterUIState.Loading
        viewModelScope.launch(dispatcher) {
            try {
                val user = repository.autoLogin()
                _uiState.postValue(RegisterUIState.Success(user, false))
            } catch (e: SocketTimeoutException) {
                _uiState.postValue(RegisterUIState.Error("Connection time expired"))
            } catch (e: UnknownHostException) {
                _uiState.postValue(RegisterUIState.Error("No internet connection"))
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> {
                        _uiState.postValue(RegisterUIState.Error("This login is already occupied"))
                    }

                    404 -> {
                        _uiState.postValue(RegisterUIState.Error("Wrong login or password"))
                    }
                }
            } catch (e: Exception) {
                Log.d("RegistrationViewModel", "${e::class}")
                _uiState.postValue(RegisterUIState.Error("Unknown error ${e::class}: ${e.message}"))
            }

        }
    }

}