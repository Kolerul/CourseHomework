package com.example.chernykhhomework.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chernykhhomework.R
import com.example.chernykhhomework.domain.entity.Auth
import com.example.chernykhhomework.domain.repository.AuthRepository
import com.example.chernykhhomework.presentation.uistate.RegisterUIState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class RegistrationFragmentViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableLiveData<RegisterUIState>(RegisterUIState.Initializing)
    val uiState: LiveData<RegisterUIState>
        get() = _uiState

    fun register(name: String, password: String) {
        _uiState.value = RegisterUIState.Loading
        val auth = Auth(name, password)
        viewModelScope.launch {
            try {
                val authorizedUserName = repository.registration(auth)
                _uiState.value = RegisterUIState.Success(authorizedUserName, true)
            } catch (e: Exception) {
                handleException(e)
            }

        }
    }

    fun logIn(name: String, password: String) {
        _uiState.value = RegisterUIState.Loading
        val auth = Auth(name, password)
        viewModelScope.launch {
            try {
                val authorizedUserName = repository.login(auth)
                _uiState.value = RegisterUIState.Success(authorizedUserName, false)
            } catch (e: Exception) {
                handleException(e)
            }

        }
    }

    fun autoLogIn() {
        _uiState.value = RegisterUIState.Loading
        viewModelScope.launch {
            try {
                val authorizedUserName = repository.autoLogin()
                _uiState.value = RegisterUIState.Success(authorizedUserName, false)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun handleException(exception: Exception) {
        when (exception) {
            is SocketTimeoutException ->
                _uiState.value =
                    RegisterUIState.Error(application.getString(R.string.connection_time_expired))

            is UnknownHostException ->
                _uiState.value =
                    RegisterUIState.Error(application.getString(R.string.no_internet_connection))

            is HttpException -> {
                when (exception.code()) {
                    400 -> _uiState.value =
                        RegisterUIState.Error(application.getString(R.string.login_occupied))

                    404 -> _uiState.value =
                        RegisterUIState.Error(application.getString(R.string.wrong_login_or_password))
                }
            }

            else -> _uiState.value =
                RegisterUIState.Error(
                    application.getString(
                        R.string.unknown_error,
                        exception::class.toString(),
                        exception.message
                    )
                )
        }
    }

}